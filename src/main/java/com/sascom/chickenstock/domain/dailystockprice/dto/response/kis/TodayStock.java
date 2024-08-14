package com.sascom.chickenstock.domain.dailystockprice.dto.response.kis;

public record TodayStock (
        String stck_bsop_date,
        String stck_clpr,
        String stck_oprc,
        String stck_hgpr,
        String stck_lwpr,
        String acml_vol,
        String acml_tr_pbmn,
        String flng_cls_code,
        String prtt_rate,
        String mod_yn,
        String prdy_vrss_sign,
        String prdy_vrss,
        String revl_issu_reas
) {
}