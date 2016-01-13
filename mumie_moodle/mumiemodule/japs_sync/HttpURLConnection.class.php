<?php
/*
httpconnection.class.php
Version 1.1

Part of the PHP class collection
http://www.sourceforge.net/projects/php-classes/
Quelle: http://www.tutorials.de/forum/php-tutorials/226367-http-ohne-curl-version-1-1-a.html

Written by: Dennis Wronka
License: LGPL
*/
class HttpURLConnection
{
	private $host;
	private $port;
	private $ssl;
	private $useragent;

	//public function __construct($host,$port=80,$ssl=false,$useragent='Java') //fuer MUMIE 1
	public function __construct($host,$port=80,$ssl=false,$useragent='JapsClient/PHP') //fuer MUMIE 2
	{       
		$this->host=$host;
		$this->port=$port;
		$this->ssl=$ssl;
		$this->useragent=$useragent;
	}

       public function __toString() //debug()
  {  
     $string .= "Object::HttpURLConnection<br>";
     return $string;
  }

	private function decodereply($reply)
	{
		$headend=strpos($reply,"\r\n\r\n")+2;
		$head=substr($reply,0,$headend);
		$httpversion=substr($head,5,3);
		$contentlength='';
		$contentlengthstart=strpos($head,'Content-Length:');
		if ($contentlengthstart!=false)
			{
				$contentlengthstart+=16;
				$contentlengthend=strpos($head,"\r\n",$contentlengthstart);
				$contentlength=substr($head,$contentlengthstart,$contentlengthend-$contentlengthstart);
			}
		if ($httpversion=='1.0')
			{
				$datastart=$headend+2;
				$body=substr($reply,$datastart,strlen($reply)-$datastart);
			}
		elseif ($httpversion=='1.1')
			{
				$encoding='';
				$encodingstart=strpos($head,'Transfer-Encoding:');
				if ($encodingstart!=false)
					{
						$encodingstart+=19;
						$encodingend=strpos($head,"\r\n",$encodingstart);
						$encoding=substr($head,$encodingstart,$encodingend-$encodingstart);
					}
				if ($encoding=='chunked')
					{
						$datasizestart=$headend+2;
						$datasizeend=strpos($reply,"\r\n",$datasizestart);
						$datasize=hexdec(trim(substr($reply,$datasizestart,$datasizeend-$datasizestart)));
						$body='';
						while ($datasize>0)
							{
								$chunkstart=$datasizeend+2;
								$body.=substr($reply,$chunkstart,$datasize);
								$datasizestart=$chunkstart+$datasize+2;
								$datasizeend=strpos($reply,"\r\n",$datasizestart);
								$datasize=hexdec(trim(substr($reply,$datasizestart,$datasizeend-$datasizestart)));
							}
					}
				else
					{
						$datastart=$headend+2;
						$datasize=$contentlength;
						$body=substr($reply,$datastart,$datasize);
					}
			}
		$code=substr($head,9,3);
		$serverstart=strpos($head,'Server:')+8;
		$serverend=strpos($head,"\r\n",$serverstart);
		$server=substr($head,$serverstart,$serverend-$serverstart);
		$contenttype='';
		$contenttypestart=strpos($head,'Content-Type:');
		if ($contenttypestart!=false)
			{
				$contenttypestart+=14;
				$contenttypeend=strpos($head,"\r\n",$contenttypestart);
				$contenttype=substr($head,$contenttypestart,$contenttypeend-$contenttypestart);
			}
		$location='';
		$locationstart=strpos($head,'Location:');
		if ($locationstart!=false)
			{
				$locationstart+=10;
				$locationend=strpos($head,"\r\n",$locationstart);
				$location=substr($head,$locationstart,$locationend-$locationstart);
				$location_array=explode('?',$location);
				$parameters='';
				if (isset($location_array[1]))
					{
						$parameters=$location_array[1];
					}
				$location=array('uri'=>$location_array[0],'parameters'=>$parameters);
				if (empty($parameters))
					{
						unset($location['parameters']);
					}
			}
		$cookies=array();
		$cookiestart=strpos($head,'Set-Cookie:');
		while ($cookiestart!=false)
			{
				$cookiestart+=12;
				$cookieend=strpos($head,"\r\n",$cookiestart);
				$cookie=substr($head,$cookiestart,$cookieend-$cookiestart);
				$cookie_array=explode(';',$cookie);
				$expirydate='';
				$path='';
				for ($x=0;$x<count($cookie_array);$x++)
					{
						$cookie_array[$x]=explode("=",$cookie_array[$x]);
						if ($x==0)
							{
								$name=$cookie_array[$x][0];
								$value=$cookie_array[$x][1];
							}
						else
							{
								if (trim($cookie_array[$x][0])=='expires')
									{
										$expirydate=array('string'=>$cookie_array[$x][1],'timestamp'=>strtotime($cookie_array[$x][1]));
									}
								elseif (trim($cookie_array[$x][0])=='Path')
									{
										$path=$cookie_array[$x][1];
									}
							}
					}
				$cookie=array('name'=>$name,'value'=>$value,'Path'=>$path,'expirydate'=>$expirydate);
				if (empty($path))
					{
						unset($cookie['Path']);
					}
				if (empty($expirydate))
					{
						unset($cookie['expirydate']);
					}
				$cookies[]=$cookie;
				$cookiestart=strpos($head,'Set-Cookie:',$cookieend);
			}
		$headdata=array('raw'=>$head,'httpversion'=>$httpversion,'code'=>$code,'server'=>$server,'contentlength'=>$contentlength,'contenttype'=>$contenttype,'location'=>$location,'cookies'=>$cookies);
		if ((empty($contentlength)) && ($contentlength!=0))
			{
				unset($headdata['contentlength']);
			}
		if (empty($contenttype))
			{
				unset($headdata['contenttype']);
			}
		if (empty($location))
			{
				unset($headdata['location']);
			}
		if (empty($cookies))
			{
				unset($headdata['cookies']);
			}
		$data=array('head'=>$headdata,'body'=>$body);
		return $data;
	}

/*===================================================*\
#                                             HEAD                                                 #
\*===================================================*/

	public function head($uri='/',$parameters=false,$cookies=false,$authuser='',$authpassword='')
	{
		if ($this->ssl==true)
			{
				$connection=@fsockopen('ssl://'.$this->host,$this->port);
			}
		else
			{
				$connection=@fsockopen($this->host,$this->port);
			}
		if ($connection==false)
			{
				return false;
			}
		if ((empty($uri)) || ($uri{0}!='/'))
			{
				$uri='/'.$uri;
			}
		if (($parameters!=false) && (!empty($parameters)))
			{
				$paramstring='?'.$parameters;
			}
		else
			{
				$paramstring='';
			}
		if (($cookies!=false) && (!empty($cookies)))
			{
				$cookiestring='Cookie: '.$cookies."\r\n";
			}
		else
			{
				$cookiestring='';
			}
		if (!empty($authuser))
			{
				$authstring='Authorization: Basic '.base64_encode($authuser.':'.$authpassword)."\r\n";
			}
		else
			{
				$authstring='';
			}
		$host=$this->host;
		if ($this->port!=80)
			{
				$host.=':'.$this->port;
			}
		fwrite($connection,'HEAD '.$uri.$paramstring." HTTP/1.1\r\nHost: ".$host."\r\nUser-Agent: ".$this->useragent."\r\n".$cookiestring.$authstring."Connection: close\r\n\r\n");
		$reply='';
		while (!feof($connection))
			{
				$reply.=@fread($connection,128);
			}
		fclose($connection);
		$data=$this->decodereply($reply);
		return $data;
	}

/*===================================================*\
#                                             GET                                                  #
\*===================================================*/


	public function get($uri='/',$parameters=false,$cookies=false,$authuser='',$authpassword='')
	{       
		if ($this->ssl==true)
			{
				$connection=@fsockopen('ssl://'.$this->host,$this->port);
			}
		else
			{
				$connection=@fsockopen($this->host,$this->port);
			}
		if ($connection==false)
			{       echo "connection==false";
				return false;
			}
		if ((empty($uri)) || ($uri{0}!='/'))
			{
				$uri='/'.$uri;
			}
		if (($parameters!=false) && (!empty($parameters)))
			{
				$paramstring='?'.$parameters;
			}
		else
			{
				$paramstring='';
			}
		if (($cookies!=false) && (!empty($cookies)))
			{
				$cookiestring='Cookie: '.$cookies."\r\n";
			}
		else
			{
				$cookiestring='';
			}
		if (!empty($authuser))
			{
				$authstring='Authorization: Basic '.base64_encode($authuser.':'.$authpassword)."\r\n";
			}
		else
			{
				$authstring='';
			}
		$host=$this->host;
		if ($this->port!=80)
			{
				$host.=':'.$this->port;
			} 
		fwrite($connection,'GET '.$uri.$paramstring." HTTP/1.1\r\nHost: ".$host."\r\nUser-Agent: ".$this->useragent."\r\n".$cookiestring.$authstring."Connection: close\r\n\r\n");
		$reply='';
		while (!feof($connection))
			{
				$reply.=@fread($connection,128);
			}
		fclose($connection);
		$data=$this->decodereply($reply);
		return $data;
	}
/*===================================================*\
#                                          POST                                                   #
\*===================================================*/
	public function post($uri='/',$parameters=false,$cookies=false,$fileparameters=false,$mimetypes=false,$authuser='',$authpassword='')
	{
		if ($this->ssl==true)
			{
				$connection=@fsockopen('ssl://'.$this->host,$this->port);
			}
		else
			{
				$connection=@fsockopen($this->host,$this->port);
			}
		if ($connection==false)
			{
				return false;
			}
		if ((empty($uri)) || ($uri{0}!='/'))
			{
				$uri='/'.$uri;
			}
		if (($cookies!=false) && (!empty($cookies)))
			{
				$cookiestring='Cookie: '.$cookies.'\r\n';
			}
		else
			{
				$cookiestring='';
			}
		if (!empty($authuser))
			{
				$authstring='Authorization: Basic '.base64_encode($authuser.':'.$authpassword)."\r\n";
			}
		else
			{
				$authstring='';
			}
		$host=$this->host;
		if ($this->port!=80)
			{
				$host.=':'.$this->port;
			}
		if (($fileparameters==false) || (empty($fileparameters))) //keine Datei spezifiziert
			{
				if (($parameters!=false) && (!empty($parameters))) //nur Parameter senden
					{
						$contentlength=strlen($parameters);
						fwrite($connection,'POST '.$uri." HTTP/1.1\r\nHost: ".$host."\r\nUser-Agent: ".$this->useragent."\r\n".$cookiestring.$authstring."Connection: close\r\n");
						fwrite($connection,"Content-Type: application/x-www-form-urlencoded\r\nContent-Length: ".$contentlength."\r\n\r\n".$parameters);
                }
				else
					{
						fwrite($connection,'POST '.$uri." HTTP/1.1\r\nHost: ".$host."\r\nUser-Agent: ".$this->useragent."\r\n".$cookiestring.$authstring."Connection: close\r\n\r\n");
					}
			}
		else  //eine Datei wurde spezifiziert
			{
				$params=explode('&',$parameters);
				for ($x=0;$x<count($params);$x++)
					{
						$params[$x]=explode('=',$params[$x]);
					}
				$fileparams=explode('&',$fileparameters);
				for ($x=0;$x<count($fileparams);$x++)
					{
						$fileparams[$x]=explode('=',$fileparams[$x]);
					}
				if (($mimetypes!=false) && (!empty($mimetypes)))
					{
						$mimetypeparams=explode(',',$mimetypes);
					}
				if (!isset($mimetypeparams))
					{
						$mimetypeparams=array();
					}
				while (count($mimetypeparams)<count($fileparams))
					{
						$mimetypeparams[]='application/octet-stream';
					}
				$boundary='-------------------------'.substr(md5(uniqid()),0,15);
				$content='';
				for ($x=0;$x<count($fileparams);$x++)
					{
						$postfile=fopen($fileparams[$x][1],'r');
						$filecontent=fread($postfile,filesize($fileparams[$x][1]));
						fclose($postfile);
						$content.='--'.$boundary."\r\n";
						$content.='Content-Disposition: form-data; name="'.$fileparams[$x][0].'"; filename="'.$fileparams[$x][1].'"'."\r\n";
						$content.='Content-Type: '.$mimetypeparams[$x]."\r\n\r\n";
						$content.=$filecontent."\r\n";
					}
				for ($x=0;$x<count($params);$x++)
					{
						$content.='--'.$boundary."\r\n";
						$content.='Content-Disposition: form-data; name="'.$params[$x][0].'"'."\r\n\r\n";
						if (!empty($params[$x][1]))
							{
								$content.=$params[$x][1]."\r\n";
							}
					}
				$content.='--'.$boundary."--\r\n";
				$contentlength=strlen($content);
				fwrite($connection,'POST '.$uri." HTTP/1.1\r\nHost: ".$host."\r\nUser-Agent: ".$this->useragent."\r\n".$cookiestring.$authstring."Connection: close\r\n");
				fwrite($connection,'Content-Type: multipart/form-data; boundary='.$boundary."\r\nContent-Length: ".$contentlength."\r\n\r\n");
				fwrite($connection,$content,$contentlength);
			}
		$reply='';
		/*while (!feof($connection))
			{
				logout_time("HttpURL vor fread ");
				$temp_reply = @fread($connection,1024);
				$ascii = ord(strrev($temp_reply));
				//$temp_reply = fgets($connection,128);
				$reply.= $temp_reply;
				logout_time("HttpURL nach fread mit inhalt=".$temp_reply."\nLetztes Zeichen war das ASCII-Zeichen mit der Nr.".$ascii);
			}*/
		//statt dessen versuchen wir:
		$reply .= @fread($connection, 1024);
		//ende des versuchs
		fclose($connection);
		$data=$this->decodereply($reply);
		return $data;
	}
}
?>