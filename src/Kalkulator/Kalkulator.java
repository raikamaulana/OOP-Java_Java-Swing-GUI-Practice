/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Kalkulator;

import BelajarGUI.Login;
import BelajarGUI.MenuUtama;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;

// Saya rancang sendiri untuk konsepan kalkulator, kode full dari GPT itu ...
// hanya yang NumberFormat aja, supaya tampilan layar cantik misal ribuan itu nanti ada titiknya.

public class Kalkulator extends javax.swing.JFrame {
    // Memanggil Login supaya bisa keluar ke menu login, serta
    // ambil fungsi yang ada di login, yaitu sizingImage()
    Login log = new Login();
    Icon iconKonfirm = new javax.swing.ImageIcon(getClass().getResource("/Assets/iconKonfirm.png"));
    
    // Method untuk Mengatur Mati Nyala Tombol, ...
    // Untuk keperluan notifikasi pesan batas angka di layar
    private void setButtonsEnabled(boolean enabled) {
        btnNol.setEnabled(enabled);
        btnSatu.setEnabled(enabled);
        btnDua.setEnabled(enabled);
        btnTiga.setEnabled(enabled);
        btnEmpat.setEnabled(enabled);
        btnLima.setEnabled(enabled);
        btnEnam.setEnabled(enabled);
        btnTujuh.setEnabled(enabled);
        btnDelapan.setEnabled(enabled);
        btnSembilan.setEnabled(enabled);

        btnTambah.setEnabled(enabled);
        btnKurang.setEnabled(enabled);
        btnKali.setEnabled(enabled);
        btnBagi.setEnabled(enabled);
        btnSamaDengan.setEnabled(enabled);
        btnC.setEnabled(enabled);
        btnB.setEnabled(enabled);
        btnE.setEnabled(enabled);
        btnTitik.setEnabled(enabled);
        btnModulus.setEnabled(enabled);
    }
    
    boolean inputAngkaPertamaMines = false; // untuk mempunyai flag ketika input angka pertama itu mines/negatif
    private void setOperatorButtonsEnabled(boolean enabled) {
        btnTambah.setEnabled(enabled);
        btnKurang.setEnabled(enabled);
        btnKali.setEnabled(enabled);
        btnBagi.setEnabled(enabled);
        btnModulus.setEnabled(enabled);
    }
    
    // 💡 Method baru: format tampilan angka agar rapi dan sesuai Locale Indonesia
    private String formatAngka(double nilai) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(15); // biar desimal panjang tetap aman
        nf.setMinimumFractionDigits(0);  // tidak memaksa “.0” kalau bukan desimal
        return nf.format(nilai);
    }

    // Method untuk mengatur penginputan tombol angka.
    // Menerima parameter angka dari tiap tombol angka yang diinput.
    // Jadi aksi tombol angka ketika diklik, akan memanggil method ini dan memasukkan angka sesuai tombol angkanya
    private void appendNumber(String angka) {
        // Menyimpan isi angka dari layar kalkulator
        String teksSekarang = textFScreen.getText();
        
        // Untuk menyimpan angka yang memiliki koma diubah menjadi titik
        // Ganti koma ke titik untuk perhitungan internal (jaga konsistensi)
        String teksUntukCek = teksSekarang.replace(".", "").replace(',', '.');

        // ----- Logika untuk pengecekan batas angka sebelum koma dan sesudah koma ----- 
        // Menyimpan posisi koma ada di index keberapa. Misal 123,12 => ada di index 3 => ada 3
        // Jika tidak ada koma, akan menyimpan -1, karna kalau 0, artinya angka pertama kan
        int indexKoma = teksUntukCek.indexOf('.');
        // Menyimpan panjang karakter. Misal 123,12 => ada 6
        int panjang = teksUntukCek.length();

        // Menyimpan panjang angka sebelum koma, pakai ternary operator
        // Jika tidak ada koma/(indexKoma == -1), maka menyimpan variabel panjang yang merupakan banyaknya angka
        // Jika ada koma (else/ :), maka panjang digitSebelum itu pakai posisi index si koma
        // Jika 123456 => 6, jika 123,12 => 3 (memakai index si koma karna angka pertama itu indexnya bukan dari 0 tapi dari 1)
        int digitSebelum = (indexKoma == -1) ? panjang : indexKoma;
        
        // Menyimpan panjang angka sesudah koma
        // Jika tidak ada koma, maka yang disimpan adalah 0
        // Jika ada koma, maka yang diisi adalah total panjang angka dikurang posisi index si koma dikurang 1
        // Dikurang 1 karena panjang angka itu ketambah sama koma, karna koma dihitung juga, makanya harus dikurang 1
        // Jika 123456 => 0, jika 123,12 => 6-3-1 = 2
        int digitSesudah = (indexKoma == -1) ? 0 : panjang - indexKoma - 1;

        // untuk memberikan nilai benar atau salah untuk logika batas ketik angka
        boolean bolehTambah = true;

        // Jika tidak ada koma
        if (indexKoma == -1) {
            // Jika tidak ada koma, dan sudah mencapai 12 angka, maka tidak boleh tambah angka lagi
            // Cth: 123456789012 (sudah 12 angka), jika mau ketik angka lagi, maka tidak bisa
            // Membatasi angka sebelum koma/tidak ada koma itu 12 angka
            // Namun jika belum ada koma walau sudah 12 angka, tetap bisa pakai koma
            if (digitSebelum >= 12) {
                bolehTambah = false;
            }
        } else { // Jika sudah ada koma (desimal)
            // Jika total angka sebelum dan sesudah koma jumlahnya 15 digit, maka tak boleh input angka lagi
            // Cth 123456789012,345 walau sudah 12 angka sebelum koma, tetap bisa pakai koma, namun hanya bisa 3 angka setelah koma jika
            // sebelum koma sudah 12 angka
            if (digitSebelum + digitSesudah >= 15) {
                bolehTambah = false;
            }
        }

        // Jika bolehTambah itu false, maka if ini berjalan, untuk memberi peringatan pesan ke layar
        if (bolehTambah == false) {
            // beri bunyi beep
            java.awt.Toolkit.getDefaultToolkit().beep();
            
            String teks15Digit = textFScreen.getText(); // disimpan dahulu, untuk menggantikan warning teks, supaya ketika warning teks selesai 
                  // ditampilkan, akan memasukkan isian sebelum pesan peringatan muncul
                  
            // Menonaktifkan semua tombol, supaya proses timer pesan tidak terganggu
            setButtonsEnabled(false);
            
            // Branching untuk tampilan pesan, jika tidak ada koma/(indexKoma == -1)
            // dan digitSebelum itu sudah sampai 12, maka IF akan true
            if (indexKoma == -1 && digitSebelum >= 12) {
                textFScreen.setText("Maksimal 12 digit");
            } else {
                textFScreen.setText("Maksimal 15 digit");
            }
            
            // Timer untuk mengembalikan teks lama setelah 1 detik (1000 ms)
            Timer timer = new Timer(1000, (ActionEvent e) -> {
                textFScreen.setText(teks15Digit);
                setButtonsEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();
            
            return; // hentikan proses, keluar dari method, dan tidak masuk ke kode di bawah ini
        }
        
        // Logika tampilan awal screen, karna saya set tampilan awal itu angka 0,
        // jika screen equals/isinya 0 dan sedang tidak diberi operator, maka screen diganti dengan num (tombol angka apapun yang ditekan)
        if (teksSekarang.equals("0") && operator.equals("")) {
            textFScreen.setText(angka);
        } else { // selain dari IF, maka teksSekarang (seluruh string angka di screen akan ditambah dengan string angka baru
                 // teksSekarang dan num itu String dan bukan Int, ibaratnya "124"+"12" = "12412"
            
            if (sedangInputAngkaBaru) {
                textFScreen.setText(angka);
                sedangInputAngkaBaru = false;
                btnSamaDengan.setEnabled(true);
            } else {
                if (teksSekarang.equals("-")) btnSamaDengan.setEnabled(true);
                if (inputAngkaPertamaMines) {
                    setOperatorButtonsEnabled(true);
                }
                textFScreen.setText(teksSekarang + angka);
            }
        }
        
        // ✨ Format tampilan biar cantik (contoh: 10000 → 10.000)
        try {
            double nilai = Double.parseDouble(textFScreen.getText().replace(".", "").replace(",", "."));
            textFScreen.setText(formatAngka(nilai));
        } catch (NumberFormatException e) {
            // Jika error (misal hanya "-" atau masih ketik koma), biarkan apa adanya
        }
    }
    
    // Method untuk memberikan aksi pada tombol tiap operator, ...
    // mempunyai parameter op, supaya di tiap tombol itu memanggil method ini dan mengisi argumen "+"/"-"/dst ke parameter
    public void aksiTombolOperator(String op) {
        // ubah format locale id yang ribuan itu ada titiknya jadi string kosong, lalu yang ada koma (desimal) jadi titik, ...
        // karna perhitungan desimal itu pakai titik, bukan koma
        double angkaSekarang = Double.parseDouble(textFScreen.getText().replace(".","").replace(',','.'));
        
        // Jika angka kedua (setelah operator lain) itu tombol kurang diklik, ...
        // maka akan set layar dengan negatif, supaya bisa masukkin bilangan negatif di input angka kedua
        if (sedangInputAngkaBaru && op.equals("-")) {
            textFScreen.setText("-"); // user ingin membuat angka kedua jadi negatif
            sedangInputAngkaBaru = false; // input angka baru false karena string negatif termasuk operand dan bukan operator
            return; // memberhentikan dan keluar dari method supaya tidak mengeksekusi kode setelah return
        }
        
        // Jika sebelumnya belum ada operator (belum pencet tombol operator) → simpan angka pertama
        if (operator.equals("")) {
            angkaPertama = angkaSekarang;
        } 
        // Jika sudah ada operator dan user baru saja mengetik angka (bukan baru ganti operator)
        else if (!sedangInputAngkaBaru) {
            angkaPertama = hitungOperator(angkaPertama, angkaSekarang, operator);
            
            // Jika pembagiNol true, maka keluar dari method (tidak melakukan apa-apa)
            // pembagiNol bisa true melalui method hitungOperator, jika di operasi pembagian itu angka kedua tidak 0, maka pembagiNol tidak akan true
            if (pembagiNol) {
                pembagiNol = false;
                return;
            }

            // tampilkan hasil
            // if (angkaPertama == Math.floor(angkaPertama)) {
                // Jika hasil bilangan bulat, tampilkan tanpa koma, double hasil di type casting ke long (bilangan bulat versi besar)
                // Math.floor akan mengembalikkan bilangan desimal yang dibulatkan ke bawah, cth: 8,2 => 8,0
                // Jika hasil itu 8,0 (bukan desimal, karna hasil dari tipe data double selalu ada koma), jika dimasukkin ...
                // ke Math.floor, hasilnya akan sama. 8,0 == 8,0
                // textFScreen.setText(String.valueOf((long) angkaPertama).replace(".", ","));
            // } else {
                // textFScreen.setText(String.valueOf(angkaPertama).replace(".", ","));
                // textFScreen.setText(String.format("%.4f", hasil).replace(".", ","));
            // }
            
            textFScreen.setText(formatAngka(angkaPertama));
        }
        
        operator = op; // simpan operator baru
        sedangInputAngkaBaru = true; // siap input angka baru
    }
    
    double angkaPertama = 0, angkaKedua = 0;
    String operator = "";
    boolean sedangInputAngkaBaru = false, pembagiNol = false;
    public double hitungOperator(double a, double b, String op) {
        switch (op) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                // Jika angka kedua ketika operator pembagi itu 0
                if (b == 0) {
                    pembagiNol = true; // set pembagiNol jadi true
                    sedangInputAngkaBaru = true; // karna harus mengulang angka kedua, maka di set true yagn artinya masih sedang input angka baru
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    setButtonsEnabled(false);

                    String teksLama = textFScreen.getText();
                    textFScreen.setText("Tidak bisa dibagi 0");

                    // Timer untuk mengembalikan teks lama setelah 2 detik (2000 ms)
                    Timer timer = new Timer(2000, (ActionEvent e) -> {
                        textFScreen.setText(teksLama);
                        setButtonsEnabled(true);
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                    return a; // langsung keluar dari method dengan nilai sebelumnya
                }
                return a / b;
            case "%":
                return a % b;
            default:
                return b;
        }
    }
    
    // Method ini merupakan konstruktor, yang dimana kontruktor ini akan menjalankan apapun saat ...
    // frame pertama kali muncul
    public Kalkulator() {
        initComponents();
        log.sizingImage(backIcon); // method dari frame Login, yaitu frame untuk menyesuaikan ukuran gambar dari label
        textFScreen.setText("0"); // Menyeset angka awal di screen dengan string angka 0
        btnSamaDengan.setEnabled(false);
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(getClass().getResource("/Assets/cursor-6.png")).getImage(), new Point(0,0), "custom cursor");
        setCursor(cursor);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        textFScreen = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnTujuh = new javax.swing.JButton();
        btnSembilan = new javax.swing.JButton();
        btnDelapan = new javax.swing.JButton();
        btnEmpat = new javax.swing.JButton();
        btnLima = new javax.swing.JButton();
        btnEnam = new javax.swing.JButton();
        btnTitik = new javax.swing.JButton();
        btnNol = new javax.swing.JButton();
        btnC = new javax.swing.JButton();
        btnSatu = new javax.swing.JButton();
        btnDua = new javax.swing.JButton();
        btnTiga = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnKali = new javax.swing.JButton();
        btnBagi = new javax.swing.JButton();
        btnSamaDengan = new javax.swing.JButton();
        btnModulus = new javax.swing.JButton();
        btnB = new javax.swing.JButton();
        btnE = new javax.swing.JButton();
        btnKurang = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        backIcon = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Kalkulator Sederhana");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/Assets/logo-unsika.png")).getImage());
        setMinimumSize(new java.awt.Dimension(457, 389));

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(437, 369));

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 5));

        textFScreen.setFont(new java.awt.Font("DS-Digital", 1, 21)); // NOI18N
        textFScreen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        textFScreen.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        textFScreen.setFocusable(false);
        textFScreen.setMargin(new java.awt.Insets(2, 15, 2, 15));
        textFScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFScreenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(textFScreen)
                .addGap(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textFScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SIMPLE CALCULATOR");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(97, 97, 97))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(300, 176));

        btnTujuh.setBackground(new java.awt.Color(204, 255, 255));
        btnTujuh.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnTujuh.setForeground(new java.awt.Color(0, 102, 102));
        btnTujuh.setText("7");
        btnTujuh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTujuh.setMinimumSize(new java.awt.Dimension(69, 30));
        btnTujuh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTujuhActionPerformed(evt);
            }
        });

        btnSembilan.setBackground(new java.awt.Color(204, 255, 255));
        btnSembilan.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnSembilan.setForeground(new java.awt.Color(0, 102, 102));
        btnSembilan.setText("9");
        btnSembilan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSembilan.setMinimumSize(new java.awt.Dimension(69, 30));
        btnSembilan.setPreferredSize(new java.awt.Dimension(69, 30));
        btnSembilan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSembilanActionPerformed(evt);
            }
        });

        btnDelapan.setBackground(new java.awt.Color(204, 255, 255));
        btnDelapan.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnDelapan.setForeground(new java.awt.Color(0, 102, 102));
        btnDelapan.setText("8");
        btnDelapan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelapan.setMinimumSize(new java.awt.Dimension(69, 30));
        btnDelapan.setPreferredSize(new java.awt.Dimension(69, 30));
        btnDelapan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelapanActionPerformed(evt);
            }
        });

        btnEmpat.setBackground(new java.awt.Color(204, 255, 255));
        btnEmpat.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnEmpat.setForeground(new java.awt.Color(0, 102, 102));
        btnEmpat.setText("4");
        btnEmpat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEmpat.setMinimumSize(new java.awt.Dimension(26, 30));
        btnEmpat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpatActionPerformed(evt);
            }
        });

        btnLima.setBackground(new java.awt.Color(204, 255, 255));
        btnLima.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnLima.setForeground(new java.awt.Color(0, 102, 102));
        btnLima.setText("5");
        btnLima.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLima.setMinimumSize(new java.awt.Dimension(69, 30));
        btnLima.setPreferredSize(new java.awt.Dimension(69, 30));
        btnLima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimaActionPerformed(evt);
            }
        });

        btnEnam.setBackground(new java.awt.Color(204, 255, 255));
        btnEnam.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnEnam.setForeground(new java.awt.Color(0, 102, 102));
        btnEnam.setText("6");
        btnEnam.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnam.setMinimumSize(new java.awt.Dimension(26, 30));
        btnEnam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnamActionPerformed(evt);
            }
        });

        btnTitik.setBackground(new java.awt.Color(204, 255, 255));
        btnTitik.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnTitik.setForeground(new java.awt.Color(0, 102, 102));
        btnTitik.setText(".");
        btnTitik.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTitik.setMinimumSize(new java.awt.Dimension(26, 30));
        btnTitik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTitikActionPerformed(evt);
            }
        });

        btnNol.setBackground(new java.awt.Color(204, 255, 255));
        btnNol.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnNol.setForeground(new java.awt.Color(0, 102, 102));
        btnNol.setText("0");
        btnNol.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNol.setMaximumSize(new java.awt.Dimension(3222, 3222));
        btnNol.setMinimumSize(new java.awt.Dimension(26, 30));
        btnNol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNolActionPerformed(evt);
            }
        });

        btnC.setBackground(new java.awt.Color(204, 255, 255));
        btnC.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnC.setForeground(new java.awt.Color(0, 102, 102));
        btnC.setText("C");
        btnC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnC.setMinimumSize(new java.awt.Dimension(26, 30));
        btnC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCActionPerformed(evt);
            }
        });

        btnSatu.setBackground(new java.awt.Color(204, 255, 255));
        btnSatu.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnSatu.setForeground(new java.awt.Color(0, 102, 102));
        btnSatu.setText("1");
        btnSatu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSatu.setMinimumSize(new java.awt.Dimension(26, 30));
        btnSatu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatuActionPerformed(evt);
            }
        });

        btnDua.setBackground(new java.awt.Color(204, 255, 255));
        btnDua.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnDua.setForeground(new java.awt.Color(0, 102, 102));
        btnDua.setText("2");
        btnDua.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDua.setMinimumSize(new java.awt.Dimension(26, 30));
        btnDua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDuaActionPerformed(evt);
            }
        });

        btnTiga.setBackground(new java.awt.Color(204, 255, 255));
        btnTiga.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnTiga.setForeground(new java.awt.Color(0, 102, 102));
        btnTiga.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTiga.setLabel("3");
        btnTiga.setMinimumSize(new java.awt.Dimension(26, 30));
        btnTiga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTigaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSatu, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEmpat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTujuh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTitik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLima, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelapan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDua, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEnam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSembilan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTiga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelapan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTujuh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSembilan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLima, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEmpat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSatu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTiga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTitik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        btnTambah.setBackground(new java.awt.Color(204, 255, 255));
        btnTambah.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 102, 102));
        btnTambah.setText("+");
        btnTambah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambah.setMinimumSize(new java.awt.Dimension(50, 30));
        btnTambah.setPreferredSize(new java.awt.Dimension(50, 30));
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnKali.setBackground(new java.awt.Color(204, 255, 255));
        btnKali.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        btnKali.setForeground(new java.awt.Color(0, 102, 102));
        btnKali.setText("*");
        btnKali.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKali.setMinimumSize(new java.awt.Dimension(50, 30));
        btnKali.setPreferredSize(new java.awt.Dimension(50, 30));
        btnKali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKaliActionPerformed(evt);
            }
        });

        btnBagi.setBackground(new java.awt.Color(204, 255, 255));
        btnBagi.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnBagi.setForeground(new java.awt.Color(0, 102, 102));
        btnBagi.setText("/");
        btnBagi.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBagi.setMinimumSize(new java.awt.Dimension(50, 30));
        btnBagi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBagiActionPerformed(evt);
            }
        });

        btnSamaDengan.setBackground(new java.awt.Color(204, 255, 255));
        btnSamaDengan.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnSamaDengan.setForeground(new java.awt.Color(0, 102, 102));
        btnSamaDengan.setText("=");
        btnSamaDengan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSamaDengan.setMinimumSize(new java.awt.Dimension(50, 30));
        btnSamaDengan.setPreferredSize(new java.awt.Dimension(50, 30));
        btnSamaDengan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSamaDenganActionPerformed(evt);
            }
        });

        btnModulus.setBackground(new java.awt.Color(204, 255, 255));
        btnModulus.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnModulus.setForeground(new java.awt.Color(0, 102, 102));
        btnModulus.setText("%");
        btnModulus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModulus.setMinimumSize(new java.awt.Dimension(50, 30));
        btnModulus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModulusActionPerformed(evt);
            }
        });

        btnB.setBackground(new java.awt.Color(204, 255, 255));
        btnB.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnB.setForeground(new java.awt.Color(0, 102, 102));
        btnB.setText("B");
        btnB.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnB.setMinimumSize(new java.awt.Dimension(50, 30));
        btnB.setPreferredSize(new java.awt.Dimension(50, 30));
        btnB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBActionPerformed(evt);
            }
        });

        btnE.setBackground(new java.awt.Color(204, 255, 255));
        btnE.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnE.setForeground(new java.awt.Color(0, 102, 102));
        btnE.setText("E");
        btnE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnE.setMinimumSize(new java.awt.Dimension(50, 30));
        btnE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEActionPerformed(evt);
            }
        });

        btnKurang.setBackground(new java.awt.Color(204, 255, 255));
        btnKurang.setFont(new java.awt.Font("DS-Digital", 1, 18)); // NOI18N
        btnKurang.setForeground(new java.awt.Color(0, 102, 102));
        btnKurang.setText("-");
        btnKurang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKurang.setMinimumSize(new java.awt.Dimension(50, 30));
        btnKurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKurangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnSamaDengan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKali, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBagi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKurang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModulus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnKurang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnKali, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(btnBagi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnModulus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSamaDengan, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnB, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        backIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/turn-back.png"))); // NOI18N
        backIcon.setText("jLabel2");
        backIcon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backIcon.setPreferredSize(new java.awt.Dimension(20, 20));
        backIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel2.setText("Raika Maulana Dwi Putra");

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel3.setText("2410631170100");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void textFScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFScreenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFScreenActionPerformed

    private void btnSembilanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSembilanActionPerformed
        appendNumber("9");
    }//GEN-LAST:event_btnSembilanActionPerformed

    private void btnSamaDenganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSamaDenganActionPerformed
        if (operator.equals("")) return; // abaikan jika belum ada operator yang diklik
        
        setOperatorButtonsEnabled(true);
        // ubah format locale id yang ribuan itu ada titiknya jadi string kosong, lalu yang ada koma (desimal) jadi titik, ...
        // karna perhitungan desimal itu pakai titik, bukan koma
        angkaKedua = Double.parseDouble(textFScreen.getText().replace(".","").replace(",", "."));
        double hasil = hitungOperator(angkaPertama, angkaKedua, operator);
        
        // Jika pembagiNol true, maka keluar dari method (tidak melakukan apa-apa)
        if (pembagiNol) {
            pembagiNol = false;
            return;
        }
        
        // hasil ditampilkan ke layar dengan format angka Indonesia, cth 12000 => 12.000
        textFScreen.setText(formatAngka(hasil));
        
        // reset supaya bisa input operasi setelah mendapatkan hasil
        angkaPertama = hasil;
        angkaKedua = 0;
        operator = "";
        sedangInputAngkaBaru = false;
        btnSamaDengan.setEnabled(false);
    }//GEN-LAST:event_btnSamaDenganActionPerformed

    private void btnModulusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModulusActionPerformed
        aksiTombolOperator("%");
    }//GEN-LAST:event_btnModulusActionPerformed

    private void btnCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCActionPerformed
        textFScreen.setText("0");
        sedangInputAngkaBaru = false;
        angkaPertama = 0;
        angkaKedua = 0;
        operator = "";
        setOperatorButtonsEnabled(true); // set tombol operator menyala semua ketika menghapus seluruh layar
    }//GEN-LAST:event_btnCActionPerformed

    private void btnNolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNolActionPerformed
        appendNumber("0");
    }//GEN-LAST:event_btnNolActionPerformed

    private void btnTitikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTitikActionPerformed
        String teksSekarang = textFScreen.getText();
        if (!teksSekarang.contains(",")) {
            textFScreen.setText(teksSekarang + ",");
        }
    }//GEN-LAST:event_btnTitikActionPerformed

    private void btnTigaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTigaActionPerformed
        appendNumber("3");
    }//GEN-LAST:event_btnTigaActionPerformed

    private void btnLimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimaActionPerformed
        appendNumber("5");
    }//GEN-LAST:event_btnLimaActionPerformed

    private void btnSatuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatuActionPerformed
        appendNumber("1");
    }//GEN-LAST:event_btnSatuActionPerformed

    private void backIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backIconMouseClicked
        Kalkulator kal = new Kalkulator();
        kal.setVisible(false);
        this.dispose();
        MenuUtama mu = new MenuUtama();
        mu.setVisible(true);
    }//GEN-LAST:event_backIconMouseClicked

    private void btnEmpatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpatActionPerformed
        appendNumber("4");
    }//GEN-LAST:event_btnEmpatActionPerformed

    private void btnEnamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnamActionPerformed
        appendNumber("6");
    }//GEN-LAST:event_btnEnamActionPerformed

    private void btnTujuhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTujuhActionPerformed
        appendNumber("7");
    }//GEN-LAST:event_btnTujuhActionPerformed

    private void btnDelapanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelapanActionPerformed
        appendNumber("8");
    }//GEN-LAST:event_btnDelapanActionPerformed

    private void btnDuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuaActionPerformed
        appendNumber("2");
    }//GEN-LAST:event_btnDuaActionPerformed

    private void btnBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBActionPerformed
        // Button B untuk menghapus satu angka terakhir setiap tombol diklik
        
        // Menyimpan string angka di layar
        String teksSekarang = textFScreen.getText();
        // Menyimpan panjang angka di layar. Cth: "12000" => 5
        int panjangTeks = teksSekarang.length();
        
        // Branching, jika teksSekarang itu tinggal "0", maka return (keluar dari method void tombol B)
        if (teksSekarang.equals("0")) {
            return;
        } 
        
        // Jika panjang karakter angka tinggal 1 karakter dan bukan angka 0, maka layar langsung di set "0"
        if (!teksSekarang.equals("0") && panjangTeks == 1 && !sedangInputAngkaBaru) {
            textFScreen.setText("0");
            sedangInputAngkaBaru = false;
            angkaPertama = 0;
            angkaKedua = 0;
            operator = "";
            return;
        } 
        
        if (panjangTeks == 2 && teksSekarang.startsWith("-")) {
            // contoh: "-2" → hapus 1 → jadi "-" (bukan "0")
            textFScreen.setText("-");
            return;
        }
        
        // Ini Fitur utama tombol B, yaitu hapus setiap satu karakter terakhir jika panjang teks lebih dari 1
        if (panjangTeks > 1) {
            // Untuk menyimpan string angka yang sudah dihapus 1 karakter terakhir
            String angkaTerakhirDihapus;
            
            // Menyimpan string teks sekarang yang sudah dihapus
            // pakai substring, itu fungsi untuk mengambil rentang string, dengan parameter pertama itu untuk index awal karakter,
            // dan parameter itu index akhir karakter yang ingin dipotong. disini index awal itu 0 karena mau ambil dari angka pertama,
            // dan index akhir itu dari panjangTeks dikurang 1, karena kita mainnya index dari 0, maka dihapus 1
            angkaTerakhirDihapus = teksSekarang.substring(0, panjangTeks - 1);
            textFScreen.setText(angkaTerakhirDihapus); // set ke layar lagi dari teks yang sudah dihapus
        }
        
        // Hapus pemisah ribuan agar logika angka tetap benar
        String teksBersih = teksSekarang.replace(".", "");

        // Ganti koma dengan titik untuk sementara
        teksBersih = teksBersih.replace(",", ".");

        // Jika ada tanda negatif di depan, simpan dulu
        boolean negatif = teksBersih.startsWith("-");
        if (negatif) {
            teksBersih = teksBersih.substring(1);
        }

        // Hilangkan satu karakter terakhir (digit atau desimal)
        if (teksBersih.length() > 1) {
            teksBersih = teksBersih.substring(0, teksBersih.length() - 1);
        } else {
            teksBersih = "0";
        }

        // Tambahkan kembali tanda negatif jika perlu
        if (negatif) {
            teksBersih = "-" + teksBersih;
        }
        
        if ((teksBersih.substring(0, 1) == "-") && teksBersih.length() == 2) {
            teksBersih = "0";
        }

        // Ubah kembali ke double (jika memungkinkan)
        double nilaiBaru;
        try {
            nilaiBaru = Double.parseDouble(teksBersih);
        } catch (NumberFormatException e) {
            nilaiBaru = 0;
        }
        

        // Tampilkan hasil dengan format angka cantik
        textFScreen.setText(formatAngka(nilaiBaru));
        
    }//GEN-LAST:event_btnBActionPerformed

    private void btnEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEActionPerformed
        int result = JOptionPane.showConfirmDialog(null, "Yakin ingin keluar?", "Konfirmasi Keluar Aplikasi", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, iconKonfirm);
        if (result == JOptionPane.NO_OPTION) {
            return;
        } else if (result == JOptionPane.YES_OPTION) {
            Kalkulator kal = new Kalkulator();
            kal.setVisible(false);
            this.dispose();
            log.setVisible(true);
        }
    }//GEN-LAST:event_btnEActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        aksiTombolOperator("+");
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnKurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKurangActionPerformed
        String teksSekarang = textFScreen.getText();
        if (teksSekarang.equals("0")) {
            textFScreen.setText("-");
            setOperatorButtonsEnabled(false);
            inputAngkaPertamaMines = true;
        } else {
            if (teksSekarang.equals("-")) {
                return;
            } else {
                aksiTombolOperator("-");
            }
        }
    }//GEN-LAST:event_btnKurangActionPerformed

    private void btnKaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKaliActionPerformed
        aksiTombolOperator("*");
    }//GEN-LAST:event_btnKaliActionPerformed

    private void btnBagiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBagiActionPerformed
        aksiTombolOperator("/");
    }//GEN-LAST:event_btnBagiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Kalkulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kalkulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kalkulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kalkulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kalkulator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backIcon;
    private javax.swing.JButton btnB;
    private javax.swing.JButton btnBagi;
    private javax.swing.JButton btnC;
    private javax.swing.JButton btnDelapan;
    private javax.swing.JButton btnDua;
    private javax.swing.JButton btnE;
    private javax.swing.JButton btnEmpat;
    private javax.swing.JButton btnEnam;
    private javax.swing.JButton btnKali;
    private javax.swing.JButton btnKurang;
    private javax.swing.JButton btnLima;
    private javax.swing.JButton btnModulus;
    private javax.swing.JButton btnNol;
    private javax.swing.JButton btnSamaDengan;
    private javax.swing.JButton btnSatu;
    private javax.swing.JButton btnSembilan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTiga;
    private javax.swing.JButton btnTitik;
    private javax.swing.JButton btnTujuh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField textFScreen;
    // End of variables declaration//GEN-END:variables
}
