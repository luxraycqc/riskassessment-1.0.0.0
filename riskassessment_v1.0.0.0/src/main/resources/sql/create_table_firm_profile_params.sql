CREATE TABLE IF NOT EXISTS `firm_profile_params`(
  `apply_id` varchar(255) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `registered_capital` decimal(18,6) DEFAULT NULL,
  `registered_year` int(4) DEFAULT NULL,
  `sells` double DEFAULT NULL,
  `AAGR` double DEFAULT NULL,
  `CGAR` double DEFAULT NULL,
  `stability_quarter` double DEFAULT NULL,
  PRIMARY KEY(`apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;