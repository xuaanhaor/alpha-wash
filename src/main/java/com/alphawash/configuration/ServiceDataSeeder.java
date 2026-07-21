package com.alphawash.configuration;

import com.alphawash.constant.ServiceCategory;
import com.alphawash.entity.ServiceItem;
import com.alphawash.repository.ServiceItemRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds toàn bộ bảng giá dịch vụ vào bảng service_item.
 * Chỉ chạy khi bảng trống (count = 0).
 */
@Component
@Order(20)
@RequiredArgsConstructor
@Slf4j
public class ServiceDataSeeder implements CommandLineRunner {

    private final ServiceItemRepository repo;

    @Override
    public void run(String... args) {
        if (repo.countByDeleteFlagFalse() > 0) {
            log.info("ServiceDataSeeder: bảng service_item đã có dữ liệu, bỏ qua seed.");
            return;
        }
        log.info("ServiceDataSeeder: đang seed dữ liệu dịch vụ...");
        repo.saveAll(buildAll());
        log.info("ServiceDataSeeder: hoàn thành.");
    }

    private static BigDecimal bd(long amount) {
        return BigDecimal.valueOf(amount);
    }

    // ── WASHING ─────────────────────────────────────────────────────────────
    private List<ServiceItem> washing() {
        return List.of(
                ServiceItem.builder()
                        .name("GÓI RỬA XE LEVEL 1")
                        .category(ServiceCategory.WASHING)
                        .priceS(bd(180_000)).priceM(bd(240_000)).priceL(bd(270_000))
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("GÓI RỬA XE LEVEL 2")
                        .category(ServiceCategory.WASHING)
                        .priceS(bd(390_000)).priceM(bd(490_000)).priceL(bd(590_000))
                        .canBeBonus(true).sortOrder(2).build(),

                ServiceItem.builder()
                        .name("GÓI RỬA XE LEVEL 3")
                        .category(ServiceCategory.WASHING)
                        .priceS(bd(790_000)).priceM(bd(890_000)).priceL(bd(990_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("RỬA XE NHANH - NHIỀU BƯỚC")
                        .category(ServiceCategory.WASHING)
                        .priceS(bd(90_000)).priceM(bd(110_000)).priceL(bd(130_000))
                        .sortOrder(4).build(),

                ServiceItem.builder()
                        .name("XỊT GẦM")
                        .category(ServiceCategory.WASHING)
                        .priceS(bd(40_000)).priceM(bd(40_000)).priceL(bd(40_000))
                        .sortOrder(5).build()
        );
    }

    // ── INTERIOR ────────────────────────────────────────────────────────────
    private List<ServiceItem> interior() {
        return List.of(
                ServiceItem.builder()
                        .name("HÚT BỤI NỘI THẤT")
                        .category(ServiceCategory.INTERIOR)
                        .priceS(bd(100_000)).priceM(bd(120_000)).priceL(bd(150_000))
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("KHỬ MÙI NỘI THẤT")
                        .category(ServiceCategory.INTERIOR)
                        .priceS(bd(270_000)).priceM(bd(270_000)).priceL(bd(270_000))
                        .sortOrder(2).build(),

                ServiceItem.builder()
                        .name("VỆ SINH NỘI THẤT CƠ BẢN")
                        .category(ServiceCategory.INTERIOR)
                        .priceS(bd(1_200_000)).priceM(bd(1_500_000)).priceL(bd(1_800_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("VỆ SINH NỘI THẤT CHUYÊN SÂU")
                        .category(ServiceCategory.INTERIOR)
                        .priceS(bd(2_000_000)).priceM(bd(2_500_000)).priceL(bd(3_000_000))
                        .sortOrder(4).build()
        );
    }

    // ── POLISHING ───────────────────────────────────────────────────────────
    private List<ServiceItem> polishing() {
        return List.of(
                ServiceItem.builder()
                        .name("HIỆU CHỈNH SƠN NHANH")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(2_000_000)).priceM(bd(2_300_000)).priceL(bd(2_500_000))
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("HIỆU CHỈNH SƠN CHUYÊN SÂU")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(3_000_000)).priceM(bd(3_300_000)).priceL(bd(3_500_000))
                        .sortOrder(2).build(),

                ServiceItem.builder()
                        .name("HIỆU CHỈNH SƠN PPF VÀ DECAL")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(2_000_000)).priceM(bd(2_300_000)).priceL(bd(2_500_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("PHỤC HỒI NHỰA NGOẠI THẤT")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(300_000)).priceM(bd(350_000)).priceL(bd(400_000))
                        .sortOrder(4).build(),

                ServiceItem.builder()
                        .name("WAX BÓNG THÂN XE")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(200_000)).priceM(bd(200_000)).priceL(bd(250_000))
                        .sortOrder(5).build(),

                ServiceItem.builder()
                        .name("LÀM SẠCH NHỰA ĐƯỜNG")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(300_000)).priceM(bd(400_000)).priceL(bd(450_000))
                        .sortOrder(6).build(),

                ServiceItem.builder()
                        .name("LÀM SẠCH CHROME")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(300_000)).priceM(bd(300_000)).priceL(bd(300_000))
                        .sortOrder(7).build(),

                ServiceItem.builder()
                        .name("CHĂM SÓC KHOANG MÁY")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(1_500_000)).priceM(bd(1_500_000)).priceL(bd(1_500_000))
                        .sortOrder(8).build(),

                ServiceItem.builder()
                        .name("CHĂM SÓC MÂM, HEO THẮNG, ĐĨA THẮNG")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(2_000_000)).priceM(bd(2_000_000)).priceL(bd(2_000_000))
                        .sortOrder(9).build(),

                ServiceItem.builder()
                        .name("CHĂM SÓC GẦM XE")
                        .category(ServiceCategory.POLISHING)
                        .priceS(bd(1_500_000)).priceM(bd(1_500_000)).priceL(bd(1_500_000))
                        .sortOrder(10).build()
        );
    }

    // ── GLASS ───────────────────────────────────────────────────────────────
    private List<ServiceItem> glass() {
        return List.of(
                ServiceItem.builder()
                        .name("CHĂM SÓC KÍNH LÁI")
                        .category(ServiceCategory.GLASS)
                        .priceS(bd(300_000)).priceM(bd(300_000)).priceL(bd(300_000))
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("CHĂM SÓC 3 KÍNH")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(500_000)).priceSUV(bd(600_000))
                        .sortOrder(2).build(),

                ServiceItem.builder()
                        .name("CHĂM SÓC TOÀN BỘ KÍNH")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(1_100_000)).priceSUV(bd(1_200_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC KÍNH LÁI 6 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(600_000)).priceSUV(bd(650_000))
                        .warranty("6 tháng").sortOrder(4).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC KÍNH LÁI 12 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceS(bd(1_000_000)).priceM(bd(1_000_000)).priceL(bd(1_000_000))
                        .warranty("12 tháng").sortOrder(5).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC 3 KÍNH 6 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(1_000_000)).priceSUV(bd(1_100_000))
                        .warranty("6 tháng")
                        .description("Tặng phủ ceramic tai gương hậu")
                        .sortOrder(6).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC 3 KÍNH 12 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(1_800_000)).priceSUV(bd(2_000_000))
                        .warranty("12 tháng").sortOrder(7).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC TOÀN BỘ KÍNH 6 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(2_000_000)).priceSUV(bd(2_200_000))
                        .warranty("6 tháng").canBeBonus(true).sortOrder(8).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC TOÀN BỘ KÍNH 12 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceSEDAN(bd(3_800_000)).priceSUV(bd(4_000_000))
                        .warranty("12 tháng").sortOrder(9).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC BẢO VỆ BỀ MẶT SƠN 12 THÁNG")
                        .category(ServiceCategory.GLASS)
                        .priceS(bd(10_050_000)).priceM(bd(10_050_000)).priceL(bd(10_050_000))
                        .warranty("12 tháng").sortOrder(10).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC BẢO VỆ MÂM")
                        .category(ServiceCategory.GLASS)
                        .priceS(bd(2_500_000)).priceM(bd(2_750_000)).priceL(bd(3_000_000))
                        .canBeBonus(true).sortOrder(11).build(),

                ServiceItem.builder()
                        .name("PHỦ CERAMIC TOÀN NHỰA (NHÁM)")
                        .category(ServiceCategory.GLASS)
                        .priceS(bd(1_500_000)).priceM(bd(1_500_000)).priceL(bd(1_500_000))
                        .canBeBonus(true).sortOrder(12).build()
        );
    }

    // ── PPF ─────────────────────────────────────────────────────────────────
    private List<ServiceItem> ppf() {
        return List.of(
                ServiceItem.builder()
                        .name("DÁN PPF FULL XE DYNO SHIELD")
                        .category(ServiceCategory.PPF)
                        .brand("STEK").typeDetail("DYNO SHIELD").warranty("5 năm")
                        .priceOverSize(bd(120_000_000))
                        .description("SUV Full Size (Range Rover và tương đương)")
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("DÁN WINDOW FILM FULL XE STEK SUPREME")
                        .category(ServiceCategory.PPF)
                        .brand("STEK").warranty("10 năm")
                        .priceSEDAN(bd(17_500_000)).priceSUV(bd(20_500_000))
                        .description("Tặng ceramic toàn bộ kính")
                        .sortOrder(2).build(),

                ServiceItem.builder()
                        .name("DÁN WINDOW FILM FULL XE STEK PREMIUM")
                        .category(ServiceCategory.PPF)
                        .brand("STEK")
                        .priceSEDAN(bd(14_500_000)).priceSUV(bd(17_500_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("DÁN WINDOW FILM FULL XE STEK PERFORMANCE")
                        .category(ServiceCategory.PPF)
                        .brand("STEK")
                        .priceSEDAN(bd(10_000_000)).priceSUV(bd(11_500_000))
                        .sortOrder(4).build(),

                ServiceItem.builder()
                        .name("DÁN WINDOW FILM FULL XE 3M CRYSTALLINE")
                        .category(ServiceCategory.PPF)
                        .brand("3M").typeDetail("CRYSTALLINE")
                        .priceSEDAN(bd(14_800_000)).priceSUV(bd(17_600_000))
                        .sortOrder(5).build(),

                ServiceItem.builder()
                        .name("DÁN WINDOW FILM FULL XE 3M ECO PREMIUM")
                        .category(ServiceCategory.PPF)
                        .brand("3M")
                        .priceSEDAN(bd(11_400_000)).priceSUV(bd(12_900_000))
                        .sortOrder(6).build()
        );
    }

    // ── COMBO ───────────────────────────────────────────────────────────────
    private List<ServiceItem> combo() {
        return List.of(
                ServiceItem.builder()
                        .name("COMBO A - VỆ SINH CHI TIẾT")
                        .category(ServiceCategory.COMBO)
                        .priceS(bd(2_500_000)).priceM(bd(2_800_000)).priceL(bd(3_000_000))
                        .sortOrder(1).build(),

                ServiceItem.builder()
                        .name("COMBO B - SẠCH SÂU SÁNG BÓNG")
                        .category(ServiceCategory.COMBO)
                        .priceS(bd(4_000_000)).priceM(bd(4_500_000)).priceL(bd(5_000_000))
                        .sortOrder(2).build(),

                ServiceItem.builder()
                        .name("COMBO C - HIỆU CHỈNH CHUYÊN SÂU")
                        .category(ServiceCategory.COMBO)
                        .priceS(bd(6_000_000)).priceM(bd(6_500_000)).priceL(bd(7_000_000))
                        .sortOrder(3).build(),

                ServiceItem.builder()
                        .name("NEW CAR")
                        .category(ServiceCategory.COMBO)
                        .priceS(bd(3_500_000)).priceM(bd(4_000_000)).priceL(bd(4_500_000))
                        .sortOrder(4).build(),

                ServiceItem.builder()
                        .name("COMBO CHĂM SÓC CHI TIẾT")
                        .category(ServiceCategory.COMBO)
                        .priceS(bd(4_500_000)).priceM(bd(4_500_000)).priceL(bd(4_500_000))
                        .sortOrder(5).build()
        );
    }

    // ── OTHER / BONUS ────────────────────────────────────────────────────────
    private List<ServiceItem> other() {
        return List.of(
                ServiceItem.builder()
                        .name("BỘ KIT CHUYÊN BIỆT DÀNH CHO XE PPF")
                        .category(ServiceCategory.OTHER)
                        .priceS(bd(2_690_000)).priceM(bd(2_690_000)).priceL(bd(2_690_000))
                        .canBeBonus(true)
                        .description("Bộ kit gồm: " +
                                "1) Dung dịch rửa xe không chạm PPF; " +
                                "2) Dung dịch tẩy côn trùng; " +
                                "3) Dung dịch tẩy nhựa đường; " +
                                "4) Dung dịch làm sạch lốp; " +
                                "5) Xịt dưỡng lốp & nhựa; " +
                                "6) Xịt dưỡng nội thất; " +
                                "7) Khăn lau đa năng Gyeon; " +
                                "8) Khăn lau kính không để lại vết.")
                        .sortOrder(1).build()
        );
    }

    private List<ServiceItem> buildAll() {
        List<ServiceItem> all = new ArrayList<>();
        all.addAll(washing());
        all.addAll(interior());
        all.addAll(polishing());
        all.addAll(glass());
        all.addAll(ppf());
        all.addAll(combo());
        all.addAll(other());
        return all;
    }
}
