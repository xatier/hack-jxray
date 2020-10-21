// This tool generates a 10-year valid license key for JXRay.
//
// Author: @xatier with the power from jadx
//
// Reverse engineering is fun. :)
//
// Usage:
//     javac HackJXray.java && java HackJXray

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class HackJXray {
	private Preferences prefs;

	// jxray keys are stored in this namespace
	private static final String[] b = {"com", "oracle"};

	// jxray checks if installCheckKey_gold is stored in installCheckKey
	// to verify the installation
	private static String installCheckKey = "";  // MASKED
	private static String installCheck_gold = "";  // MASKED

	// once launched with trial mode,
	// jxray stores the email and expire timestamp in these two keys
	private static String expiretsKey = "";  // MASKED
	private static String emailKey = "";  // MASKED

	// if a valid license is provided, jxray stores the license in this key
	// we will be overriding this key with a 10-yr key.
	private static String licenseKey = "";  // MASKED

	// the start date jxray checks against the license
	private static LocalDateTime licenseStartDate = LocalDateTime.of(2019, 1, 5, 0, 0);

	// read the jxray Preferences store
	private static Preferences getPreference() {
		try {
			Preferences userRoot = Preferences.userRoot();
			String[] strArr = b;
			for (int i = 0; i < 2; i++) {
				userRoot = userRoot.node(strArr[i]);
				userRoot.flush();
			}
			Preferences node = userRoot.node("jmvf");
			node.flush();
			return node;
		} catch (BackingStoreException e2) {
			System.err.println("JXRay installation error 5: " + e2.getMessage());
			System.exit(5);
			return null;
		}
	}

	// jxray uses this helper to decode the timestamp stored in Preferences store
	private static String decodeTimestamp(String str) {
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			sb.append((char) (str.charAt(i) - 11));
		}
		return sb.toString();
	}

	// jxray uses this helper to encode the timestamp stored in Preferences store
	private static String encodeTimestamp(String str) {
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			sb.append((char) (str.charAt(i) + 11));
		}
		return sb.toString();
	}

	// generate a valid key with X days expiration
	private static String genKey(long more_days) {
		long base = 11111111111111L;  // MASKED
		long i = base * more_days + 2222;  // MASKED
		return Long.toHexString(i);
	}

	public void do_magic() {
		// jxray preferences store
		prefs = getPreference();

		String installCheck = prefs.get(installCheckKey, "<no installCheckKey>");
		String license = prefs.get(licenseKey, "<no licenseKey>");
		String emailenc = prefs.get(emailKey, "<no emailKey>");
		String timestamp = prefs.get(expiretsKey, "no expire timestamp");
		int days = 0;
		try {
			days = (int) LocalDateTime.now().until(
				LocalDateTime.parse(decodeTimestamp(timestamp)), ChronoUnit.DAYS
			);
		} catch (DateTimeParseException e) {
			System.out.println("[-] No valid expiration timestamp stored");
		}

		// jxray checks installCheckKey against the gold
		System.out.println("[+] Reading from jxray preferences store\n");
		System.out.println("============================================");
		System.out.println("installCheck: " + installCheck + "    gold:" + installCheck_gold);
		System.out.println("license: " + license);
		System.out.println("email(encoded): " + emailenc);
		System.out.println("email: " + decodeTimestamp(emailenc));
		System.out.println(days + " days remaining (trail mode)");
		System.out.println("============================================");

		System.out.println("[+] Generating 10 year key\n");
		System.out.println("============================================");

		// give me 10 more years
		long more_days = 365*10;
		String hexLicenseKey = genKey(more_days);
		LocalDateTime t = licenseStartDate.plusDays(more_days);

		System.out.println("Generating new key: " + hexLicenseKey);
		System.out.println("Valid thru: " + t);
		System.out.println("============================================");

		prefs.put(licenseKey, hexLicenseKey);

		System.out.println("Hacked! Have a Happy Java memory analysis day! :D");
	}

	public static void main(String[] args) {
		HackJXray hack = new HackJXray();
		hack.do_magic();
	}
}
