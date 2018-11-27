package com.dl.db.perf.dao;

import com.dl.db.perf.SimpleObject;

/*
CREATE TABLE `simple_object` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `vertex` varchar(11) DEFAULT NULL,
  `uid` varchar(11) DEFAULT NULL,
  `status` varchar(11) DEFAULT NULL,
  `path` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;
 */
public interface SimpleObjectMapper extends BaseMapper<SimpleObject>{
}
