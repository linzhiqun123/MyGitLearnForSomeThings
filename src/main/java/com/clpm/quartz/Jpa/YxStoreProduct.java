package com.clpm.quartz.Jpa;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * @author hupeng
 * @date 2019-10-04
 */
@Entity
@Data
@Table(name = YxStoreProduct.TABLE_NAME)
public class YxStoreProduct implements Serializable {

    static final String TABLE_NAME = "yx_store_product";

    // 商品id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 商户Id(0为总后台管理员创建,不为0的时候是商户后台创建)
    @Column(name = "mer_id", nullable = false)
    private Integer merId;

    // 商品图片
    @Column(name = "image", nullable = false)
    @NotBlank(message = "请上传商品图片")
    private String image;

    // 轮播图
    @Column(name = "slider_image", nullable = false)
    @NotBlank(message = "请上传轮播图")
    private String sliderImage;

    // 商品名称
    @Column(name = "store_name", nullable = false)
    @NotBlank(message = "请填写商品名称")
    private String storeName;

    // 商品简介
    @Column(name = "store_info", nullable = false)
//    @NotBlank(message = "请填写商品简介")
    private String storeInfo;

    // 关键字
    @Column(name = "keyword", nullable = false)
//    @NotBlank(message = "请填写关键字")
    private String keyword;

    // 产品条码（一维码）
    @Column(name = "bar_code", nullable = false)
    private String barCode;

    // 分类id
    @Column(name = "cate_id", nullable = false)
    @NotBlank(message = "请选择分类")
    private String cateId;

    // 商品价格
    @Column(name = "price", columnDefinition = "int default 0")
//    @NotNull(message = "价格必填")
//    @Min(value = 0)
    private BigDecimal price;

    // 会员价格
    @Column(name = "vip_price", columnDefinition = "int default 0")
    //@NotNull(message = "会员价必填")
    //@Min(value = 0)
    private BigDecimal vipPrice;

    // 市场价
    @Column(name = "ot_price", columnDefinition = "int default 0")
//    @NotNull(message = "原价必填")
//    @Min(value = 0)
    private BigDecimal otPrice;

    // 邮费
    @Column(name = "postage", columnDefinition = "int default 0")
//    @NotNull(message = "邮费必填")
//    @Min(value = 0)
    private BigDecimal postage;

    // 单位名
    @Column(name = "unit_name", nullable = false)
    @NotBlank(message = "请填写单位")
    private String unitName;

    // 排序
    @Column(name = "sort", columnDefinition = "int default 0")
//    @NotNull(message = "排序必填")
//    @Min(value = 0)
    private Integer sort;

    // 销量
    @Column(name = "sales", columnDefinition = "int default 0")
//    @NotNull(message = "销量必填")
//    @Min(value = 0)
    private Integer sales;


    // 库存
    @Column(name = "stock", columnDefinition = "int default 0")
//    @NotNull(message = "库存必填")
//    @Min(value = 0)
    private Integer stock;

    // 状态（0：未上架，1：上架）
    @Column(name = "is_show")
    //@NotNull(message = "状态必须选择")
    private Integer isShow;

    @Column(name = "is_sale",nullable = false)
    //@NotNull(message = "可否下架")
    private Integer isSale;

    // 是否热卖
    @Column(name = "is_hot")
//    @NotNull(message = "热卖单品必须选择")
    private Integer isHot;

    // 是否优惠
    @Column(name = "is_benefit")
//    @NotNull(message = "优惠推荐必须选择")
    private Integer isBenefit;

    // 是否精品
    @Column(name = "is_best", columnDefinition = "int default 0")
//    @NotNull(message = "精品状态必须选择")
    private Integer isBest;

    // 是否新品
    @Column(name = "is_new", columnDefinition = "int default 0")
//    @NotNull(message = "首发新品必须选择")
    private Integer isNew;

    // 是否强推
    @Column(name = "is_must", columnDefinition = "int default 0")
    @Min(value = 0)
    private Integer isMust;

    // 产品描述
    @Column(name = "description", nullable = false)
    @NotBlank(message = "产品描述")
    private String description;

    // 添加时间
    @Column(name = "add_time", nullable = false)
    private Integer addTime;

    // 是否包邮
    @Column(name = "is_postage")
//    @NotNull(message = "包邮状态必须选择")
    private Integer isPostage;

    // 是否删除
    @Column(name = "is_del", insertable = false)
    private Integer isDel;

    // 商户是否代理 0不可代理1可代理
    @Column(name = "mer_use", nullable = false)
    private Integer merUse;

    // 获得积分
    @Column(name = "give_integral",columnDefinition = "int default 0")
//    @NotNull(message = "奖励积分不能为空")
//    @Min(value = 0)
    private BigDecimal giveIntegral;

    // 成本价
    @Column(name = "cost",columnDefinition = "int default 0")
//    @NotNull(message = "成本价不能为空")
//    @Min(value = 0)
    private BigDecimal cost;

    // 秒杀状态 0 未开启 1已开启
    @Column(name = "is_seckill", columnDefinition = "int default 0")
    private Integer isSeckill;

    // 砍价状态 0未开启 1开启
    @Column(name = "is_bargain", columnDefinition = "int default 0")
    private Integer isBargain;

    // 是否优品推荐
//    @Column(name = "is_good", columnDefinition = "int default 0")
//    private Integer isGood;
    @Column(name = "is_good")
//    @NotNull(message = "优品推荐必须选择")
    private Integer isGood;

    // 虚拟销量
    @Column(name = "ficti", columnDefinition = "int default 0")
    private Integer ficti;

    // 浏览量
    @Column(name = "browse", columnDefinition = "int default 0")
    private Integer browse;

    // 产品二维码地址(用户小程序海报)
    @Column(name = "code_path")
    private String codePath;

    // 淘宝京东1688类型
    @Column(name = "soure_link")
    private String soureLink;

    // 最高价
    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "advise_price")
//    @NotNull(message = "建议零售价不能为空")
//    @Min(value = 0)
    private BigDecimal advisePrice;

    @Column(name = "buy_price")
//    @NotNull(message = "建议零售价不能为空")
//    @Min(value = 0)
    private BigDecimal buyPrice;

    // 供应商收货地址
    @Column(name = "supplier_receive_addr_id")
    private String supplierReceiveAddrId;

    @Column(name = "supplier_receive_addr")
//    @NotBlank(message = "请选择/填写收货地址")
    private String supplierReceiveAddr;

    // 分享码地址
    @Column(name = "share_url")
    private String shareUrl;;

    // 一级分佣比例
    @Column(name = "one_rate")
    private BigDecimal oneRate;

    // 一级分佣金额
    @Column(name = "one_amount")
    private BigDecimal oneAmount;

    // 二级分佣比例
    @Column(name = "two_rate")
    private BigDecimal twoRate;

    // 二级分佣金额
    @Column(name = "two_amount")
    private BigDecimal twoAmount;


    // 员工分佣比例
    @Column(name = "staff_rate")
    private BigDecimal staffRate;

    // 新加的字段------开始

    // 供应商编码
    @Column(name = "su_code", nullable = false)
    private String suCode;

    // 运营端编码
    @Column(name = "om_code", nullable = false)
    private String omCode;

    // 供应商Id
    @Column(name = "supplier_id", nullable = false)
    private String supplierId;

    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    private String delFlag;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Timestamp createDate;

    /**
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @Column(name = "update_date")
    private Timestamp updateDate;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    // 商户编码
    @Column(name = "mc_code", nullable = false)
    private String mcCode;

    // 云商品id
    @Column(name = "om_product_id")
    private Integer omProductId;

    /**
     * 合伙人最低价格
     */
    @Column(name = "pn_min_price")
    private BigDecimal pnMinPrice;

    /**
     * 合伙人最高价格
     */
    @Column(name = "pn_max_price")
    private BigDecimal pnMaxPrice;

    /**
     * 合伙人编码
     */
    @Column(name = "pn_code")
    private String pnCode;

    /**
     * 推送目标
     */
    @Column(name = "send_target")
    private Integer sendTarget;

    /**
     * 零售价最低价格
     */
    @Column(name = "sale_min_price")
    private BigDecimal saleMinPrice;

    /**
     * 零售价最高价格
     */
    @Column(name = "sale_max_price")
    private BigDecimal saleMaxPrice;

    /**
     * 合伙人结算价
     */
    @Column(name = "pn_price")
    private BigDecimal pnPrice;

    /**
     * 合伙人是否设置过价格
     */
    @Column(name = "is_pn")
    private Integer isPn;

    /**
     * 商品类型
     */
    @Column(name="receipt_type",nullable = false)
    private Integer receiptType;

    /**
     * 分组id 非必填
     */
    @Column(name="group_id")
    private Integer groupId;



    //规格属性
    @Transient
    private String specs;

    //是否多规格
    @Column(name="is_more")
    private Integer isMore;

    //是否线下专供
    @Column(name="is_offline")
    private Integer isOffline;
    //商品价格区间
    @Column(name="price_scope")
    private String priceScope;


    /**发货方 merchant supplier**/
    @Column(name="shipper")
    private String shipper;

    /**自发库存**/
    @Column(name = "mc_stock")
    private Integer mcStock;


    /**商品推荐组id，空或零为自选推荐，大于零为组推荐**/
    @Column(name="recommend_group_id")
    private Integer recommendGroupId;

    //一对多绑定,需要绑定外键
//    @OneToMany()
//    @JoinColumn(name = "product_id")
//    private List<YxStoreProductAttrValue> valueList;

    // 新加的字段------结束

    public void copy(YxStoreProduct source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
