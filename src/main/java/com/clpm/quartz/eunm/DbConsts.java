package com.clpm.quartz.eunm;

public interface DbConsts {

    String CREATE_BY_PROPERTY = "createBy";
    String CREATE_DATE_PROPERTY = "createDate";
    String UPDATE_BY_PROPERTY = "updateBy";
    String UPDATE_DATE_PROPERTY = "updateDate";
    String DESCRIPT_PROPERTY="description";
    String CONTENT_PROPERTY="content";
    String SU_CODE_PROPERTY = "suCode";
    String OM_CODE_PROPERTY = "omCode";
    String PN_CODE_PROPERTY = "pnCode";
    String MC_CODE_PROPERTY = "mcCode";
    String DEL_FLAG_PROPERTY = "delFlag";
    String DEL_FLAG_COLUMN = "del_flag";
    String NO = "0";
    String YES = "1";
    String LOGIC_DELETE_CONDITION = "del_flag = '0'";

    String AUDIT_DEFAULT = "default";
    String AUDIT_PASSED = "passed";
}
