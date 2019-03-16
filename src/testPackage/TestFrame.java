package testPackage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class TestFrame extends JFrame {

	private JPanel contentPane;
	
	static final String VERSION = "1.0";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestFrame frame = new TestFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TestFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 74));
		lblNewLabel.setText(VERSION);
		contentPane.add(lblNewLabel, BorderLayout.CENTER);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		try {
			lblNewLabel_1.setText(new File(TestFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contentPane.add(lblNewLabel_1, BorderLayout.SOUTH);
		
//		File programFile;
//		try {
//			programFile = new File(TestFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//			System.out.println(programFile.getAbsolutePath());
//			programFile.renameTo(new File(programFile.getAbsolutePath().substring(0, programFile.getAbsolutePath().lastIndexOf("\\")) + "/oldUnturnedNPCCreator.exe"));
//			lblNewLabel_1.setText(programFile.getAbsolutePath());
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		CheckForUpdate();
	}
	public static void CheckForUpdate() {
		//Get api return
		String apiReturn = null;
		try {
			apiReturn = getLatestVersion("https://api.github.com/repos/volcanocookies/TestProgram/releases/latest");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String latestVersion = null;
		String latestDownloadURL = null;
		
		if(apiReturn==null)
			return;
		
		//Get latest version
		Matcher matcher = Pattern.compile("\"tag_name\":\"([^\"]*)").matcher(apiReturn);
	    if(matcher.find())
	    	latestVersion = matcher.group(1);
	    
	    //Get latest version download url
	    matcher.reset();
	    matcher = Pattern.compile("browser_download_url\":\"(https://github.com/VolcanoCookies/TestProgram/releases/download/[^\"]*)").matcher(apiReturn);
	    if(matcher.find())
	    	latestDownloadURL = matcher.group(1);
	    
	    if(!VERSION.equals(latestVersion)) {
			Update();
		}
	}
	
	private static void Update() {
		File programFile = null;
		try {
			programFile = new File(TestFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Get latest updater version
		String updaterDownloadURL = null;
	    Matcher matcher = null;
		try {
			matcher = Pattern.compile("browser_download_url\":\"(https://github.com/VolcanoCookies/UnturnedNPCCreatorAutoupdater/releases/download/[^\"]*)").matcher(getLatestVersion("https://api.github.com/repos/volcanocookies/UnturnedNPCCreatorAutoupdater/releases/latest"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if(matcher.find())
	    	updaterDownloadURL = matcher.group(1);
		
		//Download the Updater
		try (BufferedInputStream in = new BufferedInputStream(new URL(updaterDownloadURL).openStream());
			FileOutputStream fileOutputStream = new FileOutputStream("Updater.exe")) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
		    // handle exception
			e.printStackTrace();
		}
		
		try {
			new ProcessBuilder(programFile.getAbsolutePath().substring(0, programFile.getAbsolutePath().lastIndexOf("\\")) + "\\" + "Updater.exe", programFile.getAbsolutePath()).start();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String getLatestVersion(String url) throws IOException {
	    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	    //add headers to the connection, or check the status if desired..
	    
	    // handle error response code it occurs
	    int responseCode = connection.getResponseCode();
	    InputStream inputStream;
	    if (200 <= responseCode && responseCode <= 299) {
	        inputStream = connection.getInputStream();
	    } else {
	        inputStream = connection.getErrorStream();
	    }

	    BufferedReader in = new BufferedReader(
	        new InputStreamReader(
	            inputStream));

	    StringBuilder response = new StringBuilder();
	    String currentLine;

	    while ((currentLine = in.readLine()) != null) 
	        response.append(currentLine);

	    in.close();
	    
	    return response.toString();
	}
}

