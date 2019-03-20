package cn.net.aichain.edge.ms.utils;

public class PlaceNameUtil {
    /**
     * 从地名中提取地级市名和区县名，优先匹配在原始字符串中靠前的名称，避免因道路名中出现市名或区县名而得到错误结果
     * @param raw
     * @return
     */
    public static String[] extractCityCountyName(String raw) {
        CityCountyNames cityCountyNames = new CityCountyNames();
        String firstCityName = null;
        int firstCityIndex = raw.length();
        String firstCountyName = null;
        int firstCountyIndex = raw.length();
        String countySuffix = "";
        for (CityCountyNames.City city : cityCountyNames.getCityCountyList()) {
            String currentCityName = city.getCityName();
            if (currentCityName == null) continue;
            for (String county : city.getCountyNameList()) {
                if (raw.contains(county)) {
                    int currentIndex = raw.indexOf(county);
                    if (currentIndex < firstCountyIndex) {
                        firstCountyName = county;
                        firstCountyIndex = currentIndex;
                        countySuffix = city.getCountySuffixList().get(city.getCountyNameList().indexOf(county));
                    }
                    if (currentIndex < firstCityIndex) {
                        firstCityName = currentCityName;
                        firstCityIndex = currentIndex;
                    }
                    if (firstCityName != null && firstCityIndex == 0) {
                        return new String[]{firstCityName, firstCountyName != null ? firstCountyName + countySuffix : null};
                    }
                }
            }
            if (raw.contains(currentCityName)) {
                int currentIndex = raw.indexOf(currentCityName);
                if (currentIndex < firstCityIndex) {
                    firstCityName = currentCityName;
                    firstCityIndex = currentIndex;
                }
                if (firstCityIndex == 0) {
                    return new String[]{firstCityName, firstCountyName != null ? firstCountyName + countySuffix : null};
                }
            }
        }
        return new String[]{firstCityName, firstCountyName != null ? firstCountyName + countySuffix : null};
    }
    public static void main(String[] args) throws Exception {
        String raw = "禄口政府垃圾中转站";
        String[] result = extractCityCountyName(raw);
        System.out.println(result[0] + result[1]);
    }
}
