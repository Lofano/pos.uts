package kasir;

/**
 * Model sederhana untuk merepresentasikan satu jenis barang di toko.
 */
public class Barang {
    private final String kode;
    private final String nama;
    private final long harga;

    public Barang(String kode, String nama, long harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public long getHarga() {
        return harga;
    }

    @Override
    public String toString() {
        // Supaya tampil rapi (hanya nama barang) saat dipilih di JComboBox
        return nama;
    }
}
