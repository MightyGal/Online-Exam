/*
SQLyog Ultimate v8.55 
MySQL - 5.0.15-nt : Database - rat
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`rat` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `rat`;

/*Table structure for table `clientlog` */

DROP TABLE IF EXISTS `clientlog`;

CREATE TABLE `clientlog` (
  `client` varchar(100) default NULL,
  `command` varchar(500) default NULL,
  `date` varchar(100) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `clientlog` */

insert  into `clientlog`(`client`,`command`,`date`) values ('sdasd','dasdas','dsasdad'),('dfffds','fdfds','dfdfs'),('fdf','dffdf','fdfs'),('fdsfs','fsfsd','gsdfgs'),('fdsfd','dfsf','dfdf'),('hi','hello','April 16 2013');

/*Table structure for table `clients` */

DROP TABLE IF EXISTS `clients`;

CREATE TABLE `clients` (
  `device` varchar(100) default NULL,
  `hostname` varchar(100) default NULL,
  `hostaddr` varchar(100) default NULL,
  `hostport` varchar(100) default NULL,
  `date` varchar(100) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `clients` */

insert  into `clients`(`device`,`hostname`,`hostaddr`,`hostport`,`date`) values ('asdas','asdasd','sdadsd','asdasd','dasdasdasda'),('dfffd','dffdf','fdfdfd','ffdfdf','fdf'),('fdf','fdffd','fdfdf','fdfdf','fdfdf'),('fdff','fdfd','fddf','fdff','fdff'),('dfdfd','dfdfdf','dfdf','fdf','dfdf');

/*Table structure for table `login` */

DROP TABLE IF EXISTS `login`;

CREATE TABLE `login` (
  `id` int(11) NOT NULL auto_increment,
  `uname` varchar(50) default NULL,
  `pass` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `login` */

insert  into `login`(`id`,`uname`,`pass`) values (1,'aaa','aaaa');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
