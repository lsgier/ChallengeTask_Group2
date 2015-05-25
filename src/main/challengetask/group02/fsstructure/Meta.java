package challengetask.group02.fsstructure;

import java.io.Serializable;

public class Meta implements Serializable {
	
	private long atime;
	private long ctime;

	private static final long serialVersionUID = 1L;
	
	public Meta() {
		atime = 0;
		ctime = 0;		
	}
	
	public long getAtime() {
		return atime;
	}
	public void setAtime(long atime) {
		this.atime = atime;
	}
	public long getCtime() {
		return ctime;
	}
	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
}