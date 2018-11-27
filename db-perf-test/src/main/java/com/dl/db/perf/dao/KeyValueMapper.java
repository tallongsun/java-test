package com.dl.db.perf.dao;

import com.dl.db.perf.KeyValue;

/*
CREATE TABLE `key_value` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `keyField` varchar(11) DEFAULT NULL,
  `valueField` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
 */
public interface KeyValueMapper extends BaseMapper<KeyValue>{
}
