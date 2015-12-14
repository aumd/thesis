import com.maxmind.geoip.exception.GeoIp2Exception;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class PreProcessor {
	private File input;
	public PreProcessor(File file) {
		this.input = file;
	}

	public String timeConverter(String time) {
		String [] results = time.split(":");
		Integer finalTime = (Integer.parseInt(results [0])) * 5;
		return finalTime.toString();
	}

	public String protocolConverter(String protocol) {
		if(protocol.equals("UDP"))
			return "5";
		else
			return "10";
	}

	public void logToVector() throws FileNotFoundException, IOException, GeoIp2Exception {
		PortHash ph = new PortHash();
		IpHash ih = new IpHash();
		File vectorFile = new File("Dataset.csv");
		if (!vectorFile.exists())
			vectorFile.createNewFile();
		FileWriter fw = new FileWriter(vectorFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuilder sb;
		StringTokenizer st;
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line;
		String currentToken;
		String localIpAddress;
		String location;
		String port;
		while ((line = br.readLine()) != null){
			st = new StringTokenizer(line ," ,");
			sb = new StringBuilder();
			st.nextToken(); st.nextToken();
			st.nextToken(); //ambot kng gituyo ni ug balik2
			currentToken = (st.nextToken().toString());
			sb.append(timeConverter(currentToken)).append(" ,"); // wala'y ';' sa ila book
			sb.append(st.nextToken().toString()).append.(" ,");
			st.nextToken();
			localIpAddress = st.nextToken().toString();
			if(localIpAddress.toString().equals("0.0.0.0")) {
				continue;
			} 
			else{
				location = ih.getLocation(localIpAddress);
				if(location == null)
					continue;
				else {
					if(ih.hasLocation(location)); // wa jd ni siya buhaton according sa code sa book
					else
							ih.putLocation(location);
				}
				sb.append(ih.getValue(location).toString()).append(" ,");
				st.nextToken();
				st.nextToken(); // intentional ata ang pg.repeat
				port = st.nextToken().toString();
				if(ph.hasPort(port)); // as written in book
				else
					ph.putPort(port);
				sb.append(ph.getValue*port)).append(" ,");
				sb.append(protocolConverter(st.nextToken().toString()));
				sb.append("\n");
				bw.write(sb.toString());
			}
		}
		bw.close();
		br.close();
		ph.printMap();
		ih.printMap();
	}
}

