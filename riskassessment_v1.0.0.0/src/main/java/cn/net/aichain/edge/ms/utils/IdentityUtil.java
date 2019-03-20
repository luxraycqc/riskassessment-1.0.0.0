package cn.net.aichain.edge.ms.utils;

import java.util.Calendar;

public class IdentityUtil {
    public static int[] extractAreaCodeAgeAndGender(String identityNo) {
        if (identityNo.length() != 18) return new int[]{0, 0, 0};
        try {
            int areaCode = Integer.valueOf(identityNo.substring(0, 4));
            int bornYear = Integer.valueOf(identityNo.substring(6, 10));
            Calendar calendar = Calendar.getInstance();
            int thisYear = calendar.get(Calendar.YEAR);
            int age = thisYear - bornYear;
            int gendor = (Integer.valueOf(identityNo.substring(16, 17))) % 2;
            return new int[]{areaCode, age, gendor};
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0, 0};
        }
    }
    public static void main(String[] args) {
        int[] re = extractAreaCodeAgeAndGender("321088199504020355");
        System.out.println(re[0]);
        System.out.println(re[1]);
        System.out.println(re[2]);
    }
}
