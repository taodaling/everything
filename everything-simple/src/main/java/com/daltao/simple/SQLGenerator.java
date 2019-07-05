package com.daltao.simple;

public class SQLGenerator {
    public static void main(String[] args) {
        String[] names = ("挪车贴\n" +
                "道路救援\n" +
                "3次洗车服务包\n" +
                "6次洗车服务包\n" +
                "12次洗车服务包\n" +
                "24次洗车服务包\n" +
                "1次空调清洗\n" +
                "1次打蜡\n" +
                "1次抛光\n" +
                "1次快速精洗\n" +
                "1次精洗\n" +
                "单次小保养(15万以下车型)\n" +
                "单次小保养(15万-25万车型)\n" +
                "单次小保养(25万-40万车型)\n" +
                "单次小保养(40万及以上车型)\n" +
                "安全检测\n" +
                "安全检测（故障诊断及检测）\n" +
                "6年以上代办年检\n" +
                "六年以内代办年检\n" +
                "酒后代驾\n" +
                "安全停靠\n" +
                "四轮定位\n" +
                "四轮动平衡\n" +
                "油漆修补（国产)\n" +
                "油漆修补（进口)").split("\\n");
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String sql = String.format("INSERT INTO `litemall_supplied_good` (`suplier_id`,`update_time`,`delete_id`,`good_type`,`extend_json`,`extend_bits`,`comment`,`cost_unit_price`,`good_id`,`name`,`code`,`supplier_code`,`add_time`) VALUES (3,'2019-06-18 10:00:03',0,1,NULL,0,'%s',0,NULL,'%s','CDD-%d','0','2019-06-18 10:00:54');\n", name, name, i + 1);
            System.out.println(sql);
        }
    }
}
