package cn.net.aichain.edge.ms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityCountyNames {
    private List<City> cityCountyList;

    public List<City> getCityCountyList() {
        return cityCountyList;
    }

    public class City {
        private String cityName;
        private List<String> countyNameList;
        private List<String> countySuffixList;

        private City(String cityName) {
            switch(cityName) {
                case "南京":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("玄武", "秦淮", "建邺", "鼓楼", "浦口", "栖霞", "雨花台", "江宁", "六合","溧水", "高淳"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "区", "区", "区", "区", "区", "区", "区", "区"));
                    break;
                case "镇江":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("京口", "润州", "丹徒", "镇江新区", "丹阳", "扬中", "句容"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "", "市", "市", "市"));
                    break;
                case "常州":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("天宁", "钟楼", "新北", "武进", "金坛", "溧阳"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "区", "区", "市"));
                    break;
                case "无锡":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("梁溪", "滨湖", "新吴", "锡山", "惠山", "江阴", "宜兴"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "区", "区", "市", "市"));
                    break;
                case "苏州":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("姑苏", "虎丘", "吴中", "相城", "吴江", "张家港", "常熟", "太仓", "昆山"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "区", "区", "市", "市", "市", "市"));
                    break;
                case "扬州":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("广陵", "邗江", "江都", "仪征", "高邮", "宝应"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "市", "市", "县"));
                    break;
                case "泰州":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("海陵", "高港", "姜堰", "兴化", "泰兴", "靖江"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "市", "市", "市"));
                    break;
                case "南通":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("崇川", "港闸", "通州", "海安", "如皋", "如东", "海门", "启东"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "市", "市", "县", "市", "市"));
                    break;
                case "徐州":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("云龙", "鼓楼", "泉山", "贾汪", "铜山", "新沂", "邳州", "睢宁", "沛县", "丰县"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "区", "区", "市", "市", "县", "", ""));
                    break;
                case "连云港":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("海州", "连云区", "赣榆", "东海", "灌云", "灌南"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "", "区", "县", "县", "县"));
                    break;
                case "宿迁":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("宿城", "宿豫", "沭阳", "泗阳", "泗洪"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "县", "县", "县"));
                    break;
                case "淮安":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("清江浦", "淮阴", "淮安区", "淮安经济开发区", "洪泽", "涟水", "盱眙", "金湖"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "", "", "区", "县", "县", "县"));
                    break;
                case "盐城":
                    this.cityName = cityName;
                    this.countyNameList = new ArrayList<>(Arrays.asList("亭湖", "盐都", "大丰", "响水", "滨海", "阜宁", "射阳", "建湖", "东台"));
                    this.countySuffixList = new ArrayList<>(Arrays.asList("区", "区", "区", "县", "县", "县", "县", "县", "市"));
                    break;
                default:
                    break;
            }
        }

        public String getCityName() {
            return cityName;
        }

        public List<String> getCountyNameList() {
            return countyNameList;
        }

        public List<String> getCountySuffixList() {
            return countySuffixList;
        }

    }

    public CityCountyNames() {
        this.cityCountyList = new ArrayList<>();
        this.cityCountyList.add(new City("南京"));
        this.cityCountyList.add(new City("镇江"));
        this.cityCountyList.add(new City("常州"));
        this.cityCountyList.add(new City("无锡"));
        this.cityCountyList.add(new City("苏州"));
        this.cityCountyList.add(new City("扬州"));
        this.cityCountyList.add(new City("泰州"));
        this.cityCountyList.add(new City("南通"));
        this.cityCountyList.add(new City("徐州"));
        this.cityCountyList.add(new City("连云港"));
        this.cityCountyList.add(new City("宿迁"));
        this.cityCountyList.add(new City("淮安"));
        this.cityCountyList.add(new City("盐城"));
    }
}
