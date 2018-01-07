
public class HeaderInfo {
	int headerBits;
	String fileExtention;
	public HeaderInfo(int hB, String fE){
		headerBits = hB;
		fileExtention = fE;
	}
	public int getHeaderBits(){
		return headerBits;
	}
	public String getFileExtention(){
		return fileExtention;
	}
}
