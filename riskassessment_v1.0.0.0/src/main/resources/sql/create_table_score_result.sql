CREATE TABLE IF NOT EXISTS `score_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apply_id` varchar(50) NOT NULL DEFAULT '',
  `estimate_income` varchar(50) DEFAULT NULL,
  `credit_score` int(11) DEFAULT NULL,
  `amount_credit` int(11) DEFAULT NULL,
  `generate_time` varchar(25) DEFAULT NULL,
  `label` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;