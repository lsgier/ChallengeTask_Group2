package challengetask.group02.controllers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
//import java.util.zip.CRC32;

//import challengetask.group02.controllers.exceptions.CRCException;
import challengetask.group02.helpers.SimpleCache;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.FSModifyHelper;
import challengetask.group02.controllers.exceptions.BusyException;

//This class is used to split up the files and also fetch them
public class FileContentController implements IFileContentController {

    private PeerDHT peer;
    private FSModifyHelper dhtPutGetHelper;

    private HashMap<String, byte[]> writeBuffer;
    private HashMap<String, Number160> writeBufferBlockId;
    private HashMap<String, Integer> writeBufferCounter;

    public FileContentController(PeerDHT peer) {

        //writeBuffer = new byte[File.BLOCK_SIZE];
        //writeBufferBlockId = null;
        //writeBufferCounter = 0;
        writeBuffer = new HashMap<>();
        writeBufferBlockId = new HashMap<>();
        writeBufferCounter = new HashMap<>();

        this.peer = peer;
        dhtPutGetHelper = new FSModifyHelper(this.peer);
    }

    @Override
    public int writeFile(File file, ByteBuffer buffer, long bufSize, long writeOffset, SimpleCache<File> cache) throws BusyException {
        if (writeBuffer.get(file.getEntryName()) == null) {
            writeBuffer.put(file.getEntryName(), new byte[File.BLOCK_SIZE]);
            writeBufferBlockId.put(file.getEntryName(), null);
            writeBufferCounter.put(file.getEntryName(), 0);
        }


        if (file.getReadOnly() == true) {
            if (file.getModifierPeer().compareTo(peer.peerID()) != 0) {
                throw new BusyException(file.getEntryName() + " is busy and held by peer with ID: " + file.getModifierPeer());
            }
        }

        Random random = new Random();

        byte[] content = new byte[(int) bufSize];

        long outWriteOffset = writeOffset;

        int startBlock = (int) (writeOffset / File.BLOCK_SIZE);
        int endBlock = (int) ((bufSize + writeOffset - 1) / File.BLOCK_SIZE);

        //copy the bytebuffer (data to write) into our content array
        //assuming we the content has length bufSize, otherwise BufferUnderFlowException will be thrown
        buffer.get(content);

        ArrayList<Number160> blockIDs = file.getBlocks();

        int blockCount = blockIDs.size();

        //first block is special case
        int startBytes = 0;
        //last block is special case
        int endBytes = 0;
        //maintains the pointer where to read/write
        int position = 0;

        //had to add this because problems arise when we have a File.BLOCK_SIZE bigger than typical write size
        boolean blockCreated = false;

        for (int index = startBlock; index <= endBlock; index++) {

            //	CRC32 crc32 = new CRC32();
            Block block;
            int bytesToWrite = 0;

            //if bufferBlockId == null then get block from dht and put it inside buffer
            //if the block do not exists, create block, append to file, write file, write block, update file cache

            //if the block doesn't exist, create a new one

            if (writeBufferBlockId.get(file.getEntryName()) == null) {
                if (index > blockCount - 1) {
                    Number160 ID = Number160.createHash(UUID.randomUUID().hashCode());
                    block = new Block();
                    //	block.setChecksum(index);
                    block.setID(ID);
                    blockCount++;
                    blockCreated = true;
                } else {
                    //if the block exists, fetch it
                    block = dhtPutGetHelper.getBlockDHT(blockIDs.get(index));
                    blockCreated = false;
                }
                writeBufferBlockId.put(file.getEntryName(), block.getID());
                if (blockCreated == false) {
                    writeBuffer.put(file.getEntryName(), block.getData().clone());
                }
            } else {
                block = new Block();
                block.setID(writeBufferBlockId.get(file.getEntryName()));
            }

            //we have to make a distinction between which block we are visiting at the moment
            if (startBlock == endBlock) {
                bytesToWrite = (int) bufSize;
            } else {
                if (index == startBlock) {
                    startBytes = File.BLOCK_SIZE - (int) writeOffset % File.BLOCK_SIZE;
                    bytesToWrite = startBytes;
                } else if (index == endBlock) {

                    endBytes = (int) ((bufSize + outWriteOffset) % File.BLOCK_SIZE);

                    if (endBytes == 0) endBytes = File.BLOCK_SIZE;
                    bytesToWrite = endBytes;
                } else {
                    bytesToWrite = File.BLOCK_SIZE;
                }
            }

            System.arraycopy(content, position, writeBuffer.get(file.getEntryName()), (int) writeOffset % File.BLOCK_SIZE, bytesToWrite);
            writeBufferCounter.put(file.getEntryName(), (int) writeOffset % File.BLOCK_SIZE + bytesToWrite);

            if (writeBufferCounter.get(file.getEntryName()) == File.BLOCK_SIZE) {
                block.setData(writeBuffer.get(file.getEntryName()).clone());
                dhtPutGetHelper.putBlock(block.getID(), block);
                writeBufferCounter.put(file.getEntryName(), 0);

                writeBufferBlockId.put(file.getEntryName(), null);
                //writeBufferBlockId = null;
            }

            if (blockCreated) {
                file.addBlock(block.getID());
            }

            //offset is only for the first time
            writeOffset = 0;
            position += bytesToWrite;
        }

        file.setSize(position + outWriteOffset);

        //update meta information
        file.setCtime(System.currentTimeMillis() / 1000L);
        file.setAtime(System.currentTimeMillis() / 1000L);

        dhtPutGetHelper.putFile(file.getID(), file);

        //the size of the content that was written
        return position;
    }

    @Override
    public byte[] readFile(File file, long size, long offset) /*throws CRCException */ {

        //We don't need to read the whole file, but only "size" bytes, starting from "offset"
        byte[] content = new byte[(int) size];

        ArrayList<Number160> blocks = file.getBlocks();

        //evaulate which block offset points to we assume the first block has index 0
        //the first block spans from 0 - BLOCK_SIZE-1
        //the second block from BLOCK_SIZE - 2*BLOCK_SIZE-1 etc.
        int startBlock = (int) (offset / File.BLOCK_SIZE);

        long outOffset = offset;

        //how many blocks to read? depends on the position of the offset
        //the -1 because the byte where the offset points to is read as well
        //example offset "abcdefgh" with offset = 3 and length = 3 is "def"
        int endBlock = (int) ((size + offset - 1) / File.BLOCK_SIZE);

        if (endBlock > blocks.size() - 1) {
            endBlock = blocks.size() - 1;
            size = blocks.size() * File.BLOCK_SIZE;
        }

        //number of bytes read from the first block
        int startBytes = 0;
        //number of bytes read from the last block
        int endBytes = 0;
        int position = 0;

        for (int index = startBlock; index <= endBlock; index++) {

            Number160 ID = blocks.get(index);
            Block block = dhtPutGetHelper.getBlockDHT(ID);
            //CRC32 crc32 = new CRC32();
            int bytesToRead = 0;

            //first check if CRC is correct
            /*crc32.update(block.getData());
			if(! (crc32.getValue() == block.getChecksum()) ) {
				throw new CRCException(file.getEntryName());
			}
			*/
            //since we read sequential, sequence number checking doesn't make sense in this case
            //we have to do case distinction, if we only have one block to read, it's easily done.
            if (startBlock == endBlock) {
                bytesToRead = (int) size;
            } else {

                //if it's the first block we're reading, we only have to start to read from offset
                //last block is also special
                if (index == startBlock) {

                    startBytes = File.BLOCK_SIZE - (int) offset % File.BLOCK_SIZE;
                    bytesToRead = startBytes;
                } else if (index == endBlock) {
                    endBytes = (int) ((size + outOffset) % File.BLOCK_SIZE);

                    if (endBytes == 0) endBytes = File.BLOCK_SIZE;
                    bytesToRead = endBytes;
                } else {
                    bytesToRead = File.BLOCK_SIZE;
                }
            }

            System.arraycopy(block.getData(), (int) offset % File.BLOCK_SIZE, content, position, bytesToRead);
            offset = 0;
            position += bytesToRead;
        }

        //update meta information
        file.setAtime(System.currentTimeMillis() / 1000L);
        dhtPutGetHelper.putFile(file.getID(), file);

        return content;
    }

    @Override
    public void flush(String path, File file) {
        if (writeBufferBlockId.get(file.getEntryName()) != null) {
            Block block = new Block();
            block.setID(writeBufferBlockId.get(file.getEntryName()));
            block.setData(writeBuffer.get(file.getEntryName()).clone());
            dhtPutGetHelper.putBlock(block.getID(), block);
            writeBufferCounter.put(file.getEntryName(), 0);

            writeBufferBlockId.put(file.getEntryName(), null);
            //writeBufferBlockId = null;
        }
    }
}
