import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

/*import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;*/

public class S3Sync {
	//protected final static AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
	protected final static String bucket = "ryan-dump";
	protected static Pattern regex = Pattern.compile(".*\\.swp");
	
	public static void initialise (String path) {
		File folder = new File(path);
		
		if (!folder.isDirectory()) {
			System.out.println("Not a directory");
			return;
		}
		
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				System.out.println(file.getName() + ": " + calculateHash(file));
			}
		}
	}
	
	public static void main(String[] args) {
		initialise("/home/ryan/Pictures/Camera/Bali/LilyFlower");
		
		/*try {
			Watcher watcher = new Watcher("/tmp", S3Sync::handler);
			
			watcher.run();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void handler (Event event) {
		if (regex.matcher(event.filename.toString()).find()) return;

		if (event.kind == ENTRY_CREATE || event.kind == ENTRY_MODIFY) {
			File file = event.getFile();

			if (file.length() == 0) return;
			System.out.println(calculateHash(file));
			if (calculateHash(file) == "8O9wgeFTmsAO9bdhtPsBsw==") return;
			
			//PutObjectResult result = s3.putObject(bucket, event.filename.toString(), file);
			//System.out.println(result.getContentMd5());
			
			System.out.println("Update: " + event.filename.toString());
		} else if (event.kind == ENTRY_DELETE) {
			System.out.println("Delete: " + event.filename.toString());
		}
	}
	
	public static String calculateHash (File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream is = new FileInputStream(file);
			
			byte b;
			while ((b = (byte)is.read()) != -1) {
				md.update(b);
			}
			is.close();
			
			return Base64.getEncoder().encodeToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}