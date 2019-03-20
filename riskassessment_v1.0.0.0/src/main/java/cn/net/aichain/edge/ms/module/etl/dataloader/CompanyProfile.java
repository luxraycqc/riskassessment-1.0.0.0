package cn.net.aichain.edge.ms.module.etl.dataloader;

import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.dialect.Props;
import cn.net.aichain.edge.ms.foundation.os.OSUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;

import cn.hutool.log.LogFactory;

import java.io.File;
import java.util.ArrayList;

public final class CompanyProfile {

	//请储贤填写企业画像五维图的计算方法
	public static int[] computeCompanyProfile(final String companyId) throws Exception {
		int firmScaleScore = 0, industryStatusScore = 0, stabilityScore = 0, growthOppotunityScore = 0, creditScore = 0;

		Props props = new Props("config/etl.properties");
//		String osUrl = props.getStr("srcdb.os.mysql.jdbcUrl");
//		String osUsername = props.getStr("srcdb.os.mysql.username");
//		String osPassword = props.getStr("srcdb.os.mysql.password");
//		String taxUrl = props.getStr("srcdb.taxs.mysql.jdbcUrl");
//		String taxUsername = props.getStr("srcdb.taxs.mysql.username");
//		String taxPassword = props.getStr("srcdb.taxs.mysql.password");
		String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
		String creditUsername = props.getStr("destdb.main.mysql.username");
		String creditPassword = props.getStr("destdb.main.mysql.password");
		DruidDataSource creditDs = new DruidDataSource();
		creditDs.setUrl(creditUrl);
		creditDs.setUsername(creditUsername);
		creditDs.setPassword(creditPassword);
		Db creditDb = DbUtil.use(creditDs);
		if (companyId == null) return new int[]{0, 0, 0, 0, 0};
		int re = updateParamsTable(companyId);
		if (re != 0) return new int[]{0, 0, 0, 0, 0};

		String selectFirmProfileParams = "SELECT apply_id,registered_capital, registered_year " +
				"FROM firm_profile_params WHERE APPLY_ID = ?";
		Entity firmProfileParams = creditDb.queryOne(selectFirmProfileParams, companyId);
		String selectRankSells = "SELECT tr.rank_sells FROM (\n" +
				"SELECT t.apply_id, @curRank := IF(t.sells = @prevRank, @curRank, @incRank) AS rank_sells," +
				"@incRank := @incRank + 1, @prevRank := t.sells\n" +
				"FROM firm_profile_params t,(SELECT @curRank := 0, @prevRank := NULL, @incRank := 1) r\n" +
				"ORDER BY t.sells DESC) tr\n" +
				"WHERE tr.apply_id = ?";
		String selectRankAAGR = "SELECT tr.rank_AAGR FROM (\n" +
				"SELECT t.apply_id, @curRank := IF(t.AAGR = @prevRank, @curRank, @incRank) AS rank_AAGR," +
				"@incRank := @incRank + 1, @prevRank := t.AAGR\n" +
				"FROM firm_profile_params t,(SELECT @curRank := 0, @prevRank := NULL, @incRank := 1) r\n" +
				"ORDER BY t.AAGR DESC) tr\n" +
				"WHERE tr.apply_id = ?";
		String selectRankCGAR = "SELECT tr.rank_CGAR FROM (\n" +
				"SELECT t.apply_id, @curRank := IF(t.CGAR = @prevRank, @curRank, @incRank) AS rank_CGAR," +
				"@incRank := @incRank + 1, @prevRank := t.CGAR\n" +
				"FROM firm_profile_params t,(SELECT @curRank := 0, @prevRank := NULL, @incRank := 1) r\n" +
				"ORDER BY t.CGAR DESC) tr\n" +
				"WHERE tr.apply_id = ?";
		String selectRankStability = "SELECT tr.rank_stability FROM (\n" +
				"SELECT t.apply_id, @curRank := IF(t.stability_quarter = @prevRank, @curRank, @incRank) AS rank_stability," +
				"@incRank := @incRank + 1, @prevRank := t.stability_quarter\n" +
				"FROM firm_profile_params t,(SELECT @curRank := 0, @prevRank := NULL, @incRank := 1) r\n" +
				"ORDER BY t.stability_quarter ASC) tr\n" +
				"WHERE tr.apply_id = ?";
		if (firmProfileParams == null) {
			creditDs.close();
			return new int[]{0, 0, 0, 0, 0};
		}
		double registeredCapital = firmProfileParams.getDouble("registered_capital");
		int registeredYear = firmProfileParams.getInt("registered_year");
		long rankSells = creditDb.queryOne(selectRankSells, companyId).getLong("rank_sells");
		long rankAAGR = creditDb.queryOne(selectRankAAGR, companyId).getLong("rank_AAGR");
		long rankCGAR = creditDb.queryOne(selectRankCGAR, companyId).getLong("rank_CGAR");
		long rankStability = creditDb.queryOne(selectRankStability, companyId).getLong("rank_stability");
		long maxRankAAGR = creditDb.queryNumber("SELECT count(*) FROM firm_profile_params").longValue();
		long maxRankCGAR = maxRankAAGR;
		long maxRankSells = maxRankAAGR;
		long maxRankStability = maxRankAAGR;
			// 企业规模打分
			if (registeredCapital < 100) {
				firmScaleScore = 30 - (int) Math.floor((100 - registeredCapital) / 10);
			} else if (registeredCapital < 500) {
				firmScaleScore = 35 + (int) Math.floor((registeredCapital - 100) / 80);
			} else if (registeredCapital < 1000) {
				firmScaleScore = 40 + (int) Math.floor((registeredCapital - 500) / 100);
			} else {
				firmScaleScore = 45 + (int) Math.floor((registeredCapital - 1000) / 1000);
				if (firmScaleScore > 50) firmScaleScore = 50;
			}
			if (registeredYear < 5) {
				firmScaleScore += 30 - (int) Math.floor(5 - registeredYear);
			} else if (registeredYear < 10) {
				firmScaleScore += 35 - (int) Math.floor(registeredYear - 5);
			} else if (registeredYear < 15) {
				firmScaleScore += 40 - (int) Math.floor(registeredYear - 10);
			} else {
				if ((int) Math.floor(registeredYear - 15) > 5) {
					firmScaleScore += 50;
				} else {
					firmScaleScore += 45 + (int) Math.floor(registeredYear - 15);
				}
			}
			// 行业地位打分
			if (rankSells < Math.floor(maxRankSells * 0.25)) {
				industryStatusScore = 95;
			} else if (rankSells < Math.floor(maxRankSells * 0.5)) {
				industryStatusScore = 90;
			} else if (rankSells < Math.floor(maxRankSells * 0.75)) {
				industryStatusScore = 85;
			} else {
				industryStatusScore = 80;
			}
			// 稳定性打分
			if (rankStability < Math.floor(maxRankStability * 0.25)) {
				stabilityScore = 95;
			} else if (rankStability < Math.floor(maxRankStability * 0.5)) {
				stabilityScore = 90;
			} else if (rankStability < Math.floor(maxRankStability * 0.75)) {
				stabilityScore = 85;
			} else {
				stabilityScore = 80;
			}
			// 成长性打分
			double growthOppotunityScoreTemp;
			if (rankAAGR < Math.floor(maxRankAAGR) * 0.25) {
				growthOppotunityScoreTemp = 95 * 0.5;
			} else if (rankAAGR < Math.floor(maxRankAAGR) * 0.5) {
				growthOppotunityScoreTemp = 90 * 0.5;
			} else if (rankAAGR < Math.floor(maxRankAAGR) * 0.75) {
				growthOppotunityScoreTemp = 85 * 0.5;
			} else {
				growthOppotunityScoreTemp = 80 * 0.5;
			}
			if (rankCGAR < Math.floor(maxRankCGAR) * 0.25) {
				growthOppotunityScoreTemp += 95 * 0.5;
			} else if (rankCGAR < Math.floor(maxRankCGAR) * 0.5) {
				growthOppotunityScoreTemp += 90 * 0.5;
			} else if (rankCGAR < Math.floor(maxRankCGAR) * 0.75) {
				growthOppotunityScoreTemp += 85 * 0.5;
			} else {
				growthOppotunityScoreTemp += 80 * 0.5;
			}
			growthOppotunityScore = (int) Math.round(growthOppotunityScoreTemp);
			// 信用评分
			creditScore = (int) Math.round((firmScaleScore + stabilityScore + industryStatusScore + growthOppotunityScore) * 0.1);
		final int[] companyProfile = new int[]{firmScaleScore, industryStatusScore, stabilityScore, growthOppotunityScore, creditScore};
		creditDs.close();
		return companyProfile;
	}

	public static int updateParamsTable(final String companyId) throws Exception {
		Props props = new Props("config/etl.properties");
		String creditUrl = props.getStr("destdb.main.mysql.jdbcUrl");
		String creditUsername = props.getStr("destdb.main.mysql.username");
		String creditPassword = props.getStr("destdb.main.mysql.password");
		DruidDataSource creditDs = new DruidDataSource();
		creditDs.setUrl(creditUrl);
		creditDs.setUsername(creditUsername);
		creditDs.setPassword(creditPassword);
		Db creditDb = DbUtil.use(creditDs);
		ArrayList<String> firmProfileParamsStrList = OSUtil.executePythonAndGetResult("firm_profile.py", companyId, "/python3Lib/algorithms");
		if (firmProfileParamsStrList.size() > 0) {
			String firmProfileParamsStr = firmProfileParamsStrList.get(0);
			JSONObject firmProfileParams = new JSONObject(firmProfileParamsStr);
			String registeredCapital = firmProfileParams.getStr("registered_capital");
			int registeredYear = firmProfileParams.getInt("registered_year");
			String sells = firmProfileParams.getStr("estimate_income");
			String AAGR = firmProfileParams.getStr("AAGR");
			String CGAR = firmProfileParams.getStr("CGAR");
			String stabilityQuarter = firmProfileParams.getStr("stability_quarter");
			creditDb.execute("replace into firm_profile_params values(?,?,?,?,?,?,?)",
					companyId, registeredCapital, registeredYear, sells, AAGR, CGAR, stabilityQuarter);
			creditDs.close();
			return 0;
		} else {
			creditDs.close();
			return -1;
		}
	}

}