package com.ruoyi.pay.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.pay.domain.TReport;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报对象 t_report
 *
 * @author ruoyi
 * @date 2022-05-31
 */
@Data
public class TReportVo extends TReport {
    private static final long serialVersionUID = 1L;

    private Date startDate;

    private Date endDate;
}
