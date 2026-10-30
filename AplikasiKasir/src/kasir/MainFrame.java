package kasir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Jendela utama Aplikasi Kasir Sederhana.
 * Struktur layout:
 *  - NORTH  : header judul
 *  - CENTER : panel input barang + tabel keranjang belanja
 *  - SOUTH  : panel pembayaran (total, bayar, kembalian, tombol aksi)
 */
public class MainFrame extends JFrame {

    // Contoh daftar barang toko. Silakan ubah/tambah sesuai kebutuhan.
    private final Barang[] daftarBarang = {
        new Barang("B01", "Indomie Goreng", 3500),
        new Barang("B02", "Aqua 600ml", 4000),
        new Barang("B03", "Teh Botol Sosro", 5000),
        new Barang("B04", "Roti Tawar", 15000),
        new Barang("B05", "Kopi Kapal Api", 2000),
        new Barang("B06", "Gula Pasir 1kg", 14000),
        new Barang("B07", "Beras 1kg", 12000),
        new Barang("B08", "Minyak Goreng 1L", 18000)
    };

    // Komponen input barang
    private JComboBox<Barang> cbBarang;
    private JLabel lblHargaSatuan;
    private JSpinner spJumlah;
    private JButton btnTambah;

    // Komponen tabel keranjang
    private DefaultTableModel modelTabel;
    private JTable tblKeranjang;

    // Komponen pembayaran
    private JLabel lblTotal;
    private JTextField txtBayar;
    private JLabel lblKembalian;
    private JButton btnHitung;
    private JButton btnHapus;
    private JButton btnReset;

    private long totalBelanja = 0;

    public MainFrame() {
        setTitle("Aplikasi Kasir Sederhana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setMinimumSize(new Dimension(850, 550));
        setLayout(new BorderLayout());

        add(buatPanelHeader(), BorderLayout.NORTH);

        JPanel pnlTengah = new JPanel(new BorderLayout(10, 10));
        pnlTengah.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlTengah.add(buatPanelInput(), BorderLayout.NORTH);
        pnlTengah.add(buatPanelTabel(), BorderLayout.CENTER);
        add(pnlTengah, BorderLayout.CENTER);

        add(buatPanelBawah(), BorderLayout.SOUTH);

        if (daftarBarang.length > 0) {
            lblHargaSatuan.setText(formatRupiah(daftarBarang[0].getHarga()));
        }
    }

    // ---------- PEMBUATAN PANEL (UI) ----------

    private JPanel buatPanelHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 55, 109));
        panel.setPreferredSize(new Dimension(100, 55));

        JLabel judul = new JLabel("APLIKASI KASIR SEDERHANA");
        judul.setFont(new Font("SansSerif", Font.BOLD, 22));
        judul.setForeground(Color.WHITE);
        panel.add(judul);
        return panel;
    }

    private JPanel buatPanelInput() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Barang"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);

        // Baris 0: Pilih Barang | Harga Satuan
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        panel.add(new JLabel("Pilih Barang"), c);

        cbBarang = new JComboBox<>(daftarBarang);
        cbBarang.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Barang b = (Barang) cbBarang.getSelectedItem();
                lblHargaSatuan.setText(formatRupiah(b.getHarga()));
            }
        });
        c.gridx = 1; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        panel.add(cbBarang, c);

        c.gridx = 2; c.gridy = 0; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        panel.add(new JLabel("Harga Satuan"), c);

        lblHargaSatuan = new JLabel("Rp 0");
        lblHargaSatuan.setFont(lblHargaSatuan.getFont().deriveFont(Font.BOLD));
        c.gridx = 3; c.gridy = 0;
        panel.add(lblHargaSatuan, c);

        // Baris 1: Jumlah | Tombol Tambah
        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Jumlah"), c);

        spJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        c.gridx = 1; c.gridy = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spJumlah, c);

        btnTambah = new JButton("Tambah ke Keranjang");
        btnTambah.setBackground(new Color(46, 139, 87));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahKeKeranjang());
        c.gridx = 2; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnTambah, c);

        return panel;
    }

    private JScrollPane buatPanelTabel() {
        String[] kolom = {"No", "Kode", "Nama Barang", "Harga Satuan", "Jumlah", "Subtotal"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabel hanya untuk tampilan, tidak bisa diedit manual
            }
        };

        tblKeranjang = new JTable(modelTabel);
        tblKeranjang.setRowHeight(26);
        tblKeranjang.getTableHeader().setFont(tblKeranjang.getTableHeader().getFont().deriveFont(Font.BOLD));
        tblKeranjang.setSelectionBackground(new Color(184, 207, 229));

        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(SwingConstants.CENTER);

        tblKeranjang.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblKeranjang.getColumnModel().getColumn(0).setCellRenderer(tengah);
        tblKeranjang.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblKeranjang.getColumnModel().getColumn(1).setCellRenderer(tengah);
        tblKeranjang.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblKeranjang.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblKeranjang.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblKeranjang.getColumnModel().getColumn(4).setCellRenderer(tengah);
        tblKeranjang.getColumnModel().getColumn(5).setPreferredWidth(130);

        JScrollPane scroll = new JScrollPane(tblKeranjang);
        scroll.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));
        return scroll;
    }

    private JPanel buatPanelBawah() {
        JPanel luar = new JPanel(new BorderLayout());
        luar.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Pembayaran"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);

        // Baris 0: Total Belanja | Jumlah Bayar
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        panel.add(new JLabel("Total Belanja"), c);

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        c.gridx = 1; c.gridy = 0;
        panel.add(lblTotal, c);

        c.gridx = 2; c.gridy = 0;
        panel.add(new JLabel("Jumlah Bayar"), c);

        txtBayar = new JTextField(12);
        c.gridx = 3; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        panel.add(txtBayar, c);

        // Baris 1: Kembalian | Tombol-tombol aksi
        c.gridx = 0; c.gridy = 1; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        panel.add(new JLabel("Kembalian"), c);

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(lblKembalian.getFont().deriveFont(Font.BOLD, 16f));
        lblKembalian.setForeground(new Color(200, 0, 0));
        c.gridx = 1; c.gridy = 1;
        panel.add(lblKembalian, c);

        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        btnHitung = new JButton("Hitung Kembalian");
        btnHitung.setBackground(new Color(30, 100, 200));
        btnHitung.setForeground(Color.WHITE);
        btnHitung.setFocusPainted(false);
        btnHitung.addActionListener(e -> hitungKembalian());

        btnHapus = new JButton("Hapus Item Terpilih");
        btnHapus.setBackground(new Color(200, 60, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusItemTerpilih());

        btnReset = new JButton("Transaksi Baru");
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> transaksiBaru());

        pnlTombol.add(btnHitung);
        pnlTombol.add(btnHapus);
        pnlTombol.add(btnReset);

        c.gridx = 2; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pnlTombol, c);

        luar.add(panel, BorderLayout.CENTER);
        return luar;
    }

    // ---------- LOGIKA APLIKASI ----------

    private void tambahKeKeranjang() {
        Barang barang = (Barang) cbBarang.getSelectedItem();
        if (barang == null) {
            return;
        }
        int jumlah = (Integer) spJumlah.getValue();

        // Jika barang yang sama sudah ada di keranjang, jumlahkan saja
        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            if (modelTabel.getValueAt(i, 1).equals(barang.getKode())) {
                int jumlahBaru = (Integer) modelTabel.getValueAt(i, 4) + jumlah;
                modelTabel.setValueAt(jumlahBaru, i, 4);
                modelTabel.setValueAt(formatRupiah(barang.getHarga() * jumlahBaru), i, 5);
                hitungTotal();
                return;
            }
        }

        long subtotal = barang.getHarga() * jumlah;
        modelTabel.addRow(new Object[]{
            modelTabel.getRowCount() + 1,
            barang.getKode(),
            barang.getNama(),
            formatRupiah(barang.getHarga()),
            jumlah,
            formatRupiah(subtotal)
        });
        hitungTotal();
    }

    private void hitungTotal() {
        long total = 0;
        for (int i = 0; i < modelTabel.getRowCount(); i++) {
            String kode = (String) modelTabel.getValueAt(i, 1);
            int jumlah = (Integer) modelTabel.getValueAt(i, 4);
            Barang b = cariBarang(kode);
            if (b != null) {
                total += b.getHarga() * jumlah;
            }
        }
        totalBelanja = total;
        lblTotal.setText(formatRupiah(total));
        lblKembalian.setText("Rp 0");
        txtBayar.setText("");
    }

    private Barang cariBarang(String kode) {
        for (Barang b : daftarBarang) {
            if (b.getKode().equals(kode)) {
                return b;
            }
        }
        return null;
    }

    private void hapusItemTerpilih() {
        int baris = tblKeranjang.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih item pada tabel yang ingin dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modelTabel.removeRow(baris);
        hitungTotal();
    }

    private void hitungKembalian() {
        if (modelTabel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Keranjang masih kosong.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String teks = txtBayar.getText().trim().replace(".", "").replace(",", "");
        if (teks.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Masukkan jumlah uang bayar terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            long bayar = Long.parseLong(teks);
            if (bayar < totalBelanja) {
                lblKembalian.setText("Rp 0");
                JOptionPane.showMessageDialog(this,
                        "Jumlah uang bayar kurang dari total belanja.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            lblKembalian.setText(formatRupiah(bayar - totalBelanja));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Masukkan angka yang valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void transaksiBaru() {
        if (modelTabel.getRowCount() == 0) {
            return;
        }
        int pilih = JOptionPane.showConfirmDialog(this,
                "Mulai transaksi baru? Keranjang akan dikosongkan.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            modelTabel.setRowCount(0);
            totalBelanja = 0;
            lblTotal.setText("Rp 0");
            lblKembalian.setText("Rp 0");
            txtBayar.setText("");
        }
    }

    private String formatRupiah(long angka) {
        DecimalFormatSymbols simbol = new DecimalFormatSymbols(new Locale("in", "ID"));
        simbol.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", simbol);
        return "Rp " + df.format(angka);
    }
}
