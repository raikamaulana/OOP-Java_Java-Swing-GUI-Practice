/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PenentuJumlahHari;

public class HitungHari {
    public int hitung(int tahun, String bulan) {
        int jumlahHari = 0;
        switch (bulan) {
            case "Januari":
            case "Maret":
            case "Mei":
            case "Juli":
            case "Agustus":
            case "Oktober":
            case "Desember":
                jumlahHari = 31;
                break;
            case "April":
            case "Juni":
            case "September":
            case "November":
                jumlahHari = 30;
                break;
            case "Februari":
                // Tahun Kabisat itu ada 29 hari
                // Tahun Kabisat itu
                // 1. Jika Tahun habis dibagi 4 dan tidak habis dibagi 100, maka Kabisat
                // 2. Jika Tahun habis dibagi 400, maka kabisat
                // 3, Jika Tahun habis dibagi 4 dan habis dibagi 100, dan tidak habis dibagi 400, maka bukan kabisat
                if ((tahun % 4 == 0 && tahun % 100 != 0) || (tahun % 400 == 0)) {
                    jumlahHari = 29;
                } else {
                    jumlahHari = 28;
                }
                break;
        }
        return jumlahHari;
    }
}
