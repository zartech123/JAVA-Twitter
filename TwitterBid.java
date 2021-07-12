import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;



public class TwitterBid {

	private static Logger logger = Logger.getLogger(TwitterBid.class);
	private ConnectionImpl connDb = new ConnectionImpl();
	private Properties propXML = new Properties();
	private Object[][] results_inquiry = new Object[20][6];
	private Object[][] results_inquiry2 = new Object[5][2];
	private Object[][] results_inquiry3 = new Object[5][2];
	private Object[][] results_inquiry4 = new Object[1][1];
	private Object[][] results_inquiry5 = new Object[2][2];
	private Object[][] results_inquiry6 = new Object[1][2];
	private Object[][] results_inquiry7 = new Object[20][2];
	private Object[][] results_inquiry8 = new Object[20][1];
	private Object[][] results_inquiry9 = new Object[1][3];
	private Object[][] results_inquiry10 = new Object[20][4];
	private Map <Integer, String> field_inquiry = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry2 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry3 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry4 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry5 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry6 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry7 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry8 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry9 = new HashMap<Integer, String>();
	private Map <Integer, String> field_inquiry10 = new HashMap<Integer, String>();
	private String RegisterKeyword = "";
	private String UpdateKeyword = "";
	private String RegisterRegex = "";
	private String UpdateRegex = "";
	private String BidRegex = "";
	private int NominalBidRegexIndex;
	private int MSISDNRegisterRegexIndex;
	private int CityRegisterRegexIndex;
	private int NameRegisterRegexIndex;
	private int MSISDNUpdateRegexIndex;
	private int CityUpdateRegexIndex;
	private int NameUpdateRegexIndex;
	private String name;
	private String msisdn;
	private String city;
	private String keyword;
	private int nominal;
	private int minimum;
	private int currentMinimum;
	private int hashTagId;
	private int userId;
	private int userStatus;
	private int bidId;
	private int buy_it_now;
	private String hashTag;
	private String message;
	private String userName;
	private String createdAt;
	private int userTweet;
	private String _WRONG_HASHTAG;
	private String _WRONG_KEYWORD;
	private String _REG_WRONG_FORMAT;
	private String _UPDATE_WRONG_FORMAT;
	private String _REG_VALID_FORMAT;
	private String _REG_USER_HAVE_REGISTERED;
	private String _UPDATE_VALID_FORMAT;
	private String _UPDATE_USER_NOT_REGISTERED;
	private String _BID_USER_NOT_REGISTERED;
	private String _BUY_IT_NOW;
	private String _OUT_BID;
	private String _VALID_NOMINAL_BID;
	private String _BLACKLIST_USER;
	private String _BID_WRONG_FORMAT;
	private int minimumTweet;
	private int minimumUserCreated;
	private Twitter twitter;
	private String imageDirectory;
	private int trafficId;
	
	
	public TwitterBid()
	{
		try
		{
			propXML.load(new FileInputStream(System.getProperty("user.dir")+"/config/properties.prop"));
	
			PropertyConfigurator.configure(System.getProperty("user.dir")+"/config/log4j.properties");    			
			while(connDb.isConnected()==false)
			{	
				connDb.setProperties(propXML);
				connDb.setUrl();
				connDb.setConnection();
			}				
			ConfigurationBuilder cb = new ConfigurationBuilder();
		    cb.setDebugEnabled(true)
		            .setOAuthConsumerKey("BpeNxWxqEuYsZ6hYFtcXgF02k")
		            .setOAuthConsumerSecret("8xmcaNUyicIHohuYyMNolGktJwD6v2CyaCO3XYWz1QBiWS44O7")
		            .setOAuthAccessToken("20971212-4HKLWZtTSaYV9uVyR4IzmrPYKamIchJ4e8SRwjmMk")
		            .setOAuthAccessTokenSecret("pIwqrkcXAcRTmgDzCL1WSp7ACLtov2uD1wMsQURnpz9GC");
			
		    TwitterFactory tf = new TwitterFactory(cb.build());
		    twitter = tf.getInstance();
			getRegister();
			getUpdate();
			getBid();
			getPendingBid();
			getTraffic();

		}
		catch (FileNotFoundException e) 
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		}	
		catch (IOException e) 
		{
			logger.error(this.getClass().getName()+" "+e.getMessage());
		}	
	}
	
	public void getPendingBid()
	{
		field_inquiry10 = new TreeMap<Integer, String>();
		field_inquiry10.put(0, "id_bid");
		field_inquiry10.put(1, "id_hashtag");
		field_inquiry10.put(2, "hashtag");
		field_inquiry10.put(3, "description");

		results_inquiry10=connDb.getQuery("SELECT a.id as id_bid, b.id as id_hashtag, hashtag, description FROM bid a, hashtag b WHERE a.id=b.id_bid AND start_time<NOW() AND end_time>NOW() AND a.status=1 AND b.status=1", new Object[]{0,0,"",""}, field_inquiry10, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				System.out.println("Send Tweet "+results_inquiry10[i][2].toString()+results_inquiry10[i][3].toString());
//				postMessageImageTweet(results_inquiry10[i][2].toString()+results_inquiry10[i][3].toString(),new Integer(results_inquiry10[i][1].toString()).intValue());
				connDb.updateQuery("update hashtag set status=?, created_date=now() where id=?",new Object[]{"2",new Integer(results_inquiry10[i][1].toString()).intValue()});			
				connDb.updateQuery("update bid set status=?, created_date=now() where id=?",new Object[]{"2",new Integer(results_inquiry10[i][0].toString()).intValue()});			
			}	
		}

	}
	
	public void checkUser(String input)
	{
		field_inquiry6 = new TreeMap<Integer, String>();
		field_inquiry6.put(0, "id");
		field_inquiry6.put(1, "status");

		results_inquiry6=connDb.getQuery("SELECT id, status from user_bid where username='"+input+"'", new Object[]{0,0}, field_inquiry6, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			userId = new Integer(results_inquiry6[0][0].toString()).intValue();
			userStatus = new Integer(results_inquiry6[0][1].toString()).intValue();
		}
		
	}
	
	public void getTraffic()
	{
		field_inquiry9 = new TreeMap<Integer, String>();
		field_inquiry9.put(0, "id");
		field_inquiry9.put(1, "message");
		field_inquiry9.put(2, "username");

		results_inquiry9=connDb.getQuery("SELECT a.id, message, b.username FROM traffic a, user_bid b WHERE a.id_user=b.id AND a.status=1 order by a.created_date desc limit 1", new Object[]{0,"",""}, field_inquiry9, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			trafficId = new Integer(results_inquiry9[0][0].toString()).intValue();
			message = results_inquiry9[0][1].toString();
			userName = results_inquiry9[0][2].toString();
			System.out.println(parse());
		}

	}
	
	public void getUserProfile(String username)
	{
		try {
		    User user = twitter.showUser(username);
            if (user.getStatus() != null) 
            {
            	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            	createdAt = format.format(user.getCreatedAt());
            	userTweet = user.getStatusesCount();
            } 
        } catch (TwitterException te) {
            te.printStackTrace();
            logger.error(this.getClass().getName()+" "+ te.getMessage());
        }		
	}
	
	public void postMessageTweet(String statusMessage)
	{
		try
		{
			StatusUpdate update = new StatusUpdate(statusMessage);
			Status status = twitter.updateStatus(update);
        } catch (TwitterException te) {
            te.printStackTrace();
            logger.error(this.getClass().getName()+" "+ te.getMessage());
        }		
	}
	
	public void getTweet()
	{
		try
		{
			List<Status> statuses;
            String user;
            user = "FWDfootballshop";
            statuses = twitter.getUserTimeline(user);
            System.out.println("Showing @" + user + "'s user timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - Text : " + status.getText()+ " - getInReplyToStatusId : "+status.getInReplyToStatusId()+" - ID : ");
            }
        } catch (TwitterException te) {
	        te.printStackTrace();
	        logger.error(this.getClass().getName()+" "+ te.getMessage());
	    }		
		
	}
	
	public void postMessageImageTweet(String statusMessage, int id)
	{
		try
		{
			StatusUpdate update = new StatusUpdate(statusMessage);
			field_inquiry8 = new TreeMap<Integer, String>();
			field_inquiry8.put(0, "url");

			results_inquiry8=connDb.getQuery("SELECT concat('"+imageDirectory+"',b.url) as url FROM hashtag a, photo b WHERE a.id=b.id_hashtag AND id_hashtag="+id, new Object[]{""}, field_inquiry8, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
	            long[] mediaIds = new long[connDb.getRowCount(0)];
				for(int i=0;i<connDb.getRowCount(0);i++)
				{
					UploadedMedia media = twitter.uploadMedia(new File(results_inquiry8[i][0].toString()));
					mediaIds[i] = media.getMediaId();
				}
				update.setMediaIds(mediaIds);
			}	
			
			Status status = twitter.updateStatus(update);
        } catch (TwitterException te) {
            te.printStackTrace();
            logger.error(this.getClass().getName()+" "+ te.getMessage());
        }		
	}

	
	public void insertTraffic()
	{
		connDb.updateQuery("insert into traffic (id_user, message, url, id_hashtag, status,id_bid,response,created_date) values (?,?,?,?,?,?,'',now())",new Object[]{userId,message,"",hashTagId,"0",bidId});
	}

	public void RegisterUser()
	{
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		int days = 0;
		try
		{
			Date date1 = myFormat.parse(createdAt);
			Date today = Calendar.getInstance().getTime();
			long diff = today.getTime() - date1.getTime();
			days = new Long(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)).intValue();
		} 
		catch (ParseException e) 
		{
            logger.error(this.getClass().getName()+" "+ e.getMessage());
		}
		if(userTweet<minimumTweet || days<minimumUserCreated)
		{	
			connDb.updateQuery("insert into user_bid (name, city, msisdn, username, status,created_at,tweet,created_date) values (?,?,?,?,2,?,?,now())",new Object[]{name,city,msisdn,userName,createdAt,userTweet});
		}
		else
		{
			connDb.updateQuery("insert into user_bid (name, city, msisdn, username, status,created_at,tweet,created_date) values (?,?,?,?,1,?,?,now())",new Object[]{name,city,msisdn,userName,createdAt,userTweet});			
		}
	}

	public void UpdateUser()
	{
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		int days = 0;
		try
		{
			Date date1 = myFormat.parse(createdAt);
			Date today = Calendar.getInstance().getTime();
			long diff = today.getTime() - date1.getTime();
			days = new Long(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)).intValue();
		} 
		catch (ParseException e) 
		{
            logger.error(this.getClass().getName()+" "+ e.getMessage());
		}
		if(userTweet<minimumTweet || days<minimumUserCreated)
		{	
			connDb.updateQuery("update user_bid set name=?, city=?, msisdn=?, created_at=?, tweet=?, status=2, created_date=now() where id=?",new Object[]{name,city,msisdn,createdAt,userTweet,userId});
		}
		else
		{
			connDb.updateQuery("update user_bid set name=?, city=?, msisdn=?, created_at=?, tweet=?, status=1, created_date=now() where id=?",new Object[]{name,city,msisdn,createdAt,userTweet,userId});			
		}
	}

	public void UpdateHashTag()
	{
		connDb.updateQuery("update hashtag set id_winner=?, current_minimum=?, created_date=now() where id=?",new Object[]{userId,currentMinimum,hashTagId});
	}


	public void getNotification()
	{
		field_inquiry7 = new TreeMap<Integer, String>();
		field_inquiry7.put(0, "notification");
		field_inquiry7.put(1, "value");

		results_inquiry7=connDb.getQuery("SELECT notification, value FROM notification", new Object[]{"",""}, field_inquiry7, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				if(results_inquiry7[i][0].toString().compareTo("_WRONG_HASHTAG")==0)
				{
					_WRONG_HASHTAG = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_WRONG_KEYWORD")==0)
				{
					_WRONG_KEYWORD = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_REG_WRONG_FORMAT")==0)
				{
					_REG_WRONG_FORMAT = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_UPDATE_WRONG_FORMAT")==0)
				{
					_UPDATE_WRONG_FORMAT = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_REG_VALID_FORMAT")==0)
				{
					_REG_VALID_FORMAT = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_REG_USER_HAVE_REGISTERED")==0)
				{
					_REG_USER_HAVE_REGISTERED = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_UPDATE_VALID_FORMAT")==0)
				{
					_UPDATE_VALID_FORMAT = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_UPDATE_USER_NOT_REGISTERED")==0)
				{
					_UPDATE_USER_NOT_REGISTERED = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_BID_USER_NOT_REGISTERED")==0)
				{
					_BID_USER_NOT_REGISTERED = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_BUY_IT_NOW")==0)
				{
					_BUY_IT_NOW = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_OUT_BID")==0)
				{
					_OUT_BID = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_VALID_NOMINAL_BID")==0)
				{
					_VALID_NOMINAL_BID = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_BLACKLIST_USER")==0)
				{
					_BLACKLIST_USER = results_inquiry7[i][1].toString();
				}
				if(results_inquiry7[i][0].toString().compareTo("_BID_WRONG_FORMAT")==0)
				{
					_BID_WRONG_FORMAT = results_inquiry7[i][1].toString();
				}
				
				
			}	
		}
		
	}
	
	public String checkKeyword(String input)
	{
		String result = "";
		
		results_inquiry4=connDb.getQuery("SELECT b.hashtag FROM bid a, hashtag b WHERE a.id=b.id_bid AND a.status=2 AND b.status=2 AND '"+input+"' LIKE CONCAT('#',b.hashtag,' "+RegisterKeyword+"%')", new Object[]{""}, field_inquiry4, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			result = RegisterKeyword;
			keyword = "Register";
		}

		if(result=="")
		{	
			results_inquiry4=connDb.getQuery("SELECT b.hashtag FROM bid a, hashtag b WHERE a.id=b.id_bid AND a.status=2 AND b.status=2 AND '"+input+"' LIKE CONCAT('#',b.hashtag,' "+UpdateKeyword+"%')", new Object[]{""}, field_inquiry4, new Object[]{},0);
			if(connDb.getRowCount(0)>0)
			{    		
				result = UpdateKeyword;
				keyword = "Update";
			}
			if(result=="")
			{
				keyword = "Bid";
				result = "BID";				
			}
		}	
		return result;
	}
	
	public String getHashTag(String input)
	{
		field_inquiry = new TreeMap<Integer, String>();
		field_inquiry.put(0, "hashtag");
		field_inquiry.put(1, "minimum");
		field_inquiry.put(2, "buy_it_now");
		field_inquiry.put(3, "current_minimum");
		field_inquiry.put(4, "hashTagId");
		field_inquiry.put(5, "bidId");

		String result = "";

		results_inquiry=connDb.getQuery("SELECT distinct b.hashtag, a.minimum, a.buy_it_now, b.current_minimum, b.id as hashTagId, a.id as bidId FROM bid a, hashtag b WHERE a.id=b.id_bid AND a.status=2 AND b.status=2 AND '"+input+"' LIKE CONCAT('#',b.hashtag,' %')", new Object[]{"",0,0,0,0,0}, field_inquiry, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			result = results_inquiry[0][0].toString();
			hashTag=result;
			minimum = new Integer(results_inquiry[0][1].toString()).intValue();
			buy_it_now = new Integer(results_inquiry[0][2].toString()).intValue();
			currentMinimum = new Integer(results_inquiry[0][3].toString()).intValue();
			hashTagId = new Integer(results_inquiry[0][4].toString()).intValue();
			bidId = new Integer(results_inquiry[0][5].toString()).intValue();
		}
		
		return result;
	}
	
	public void getRegister()
	{
		field_inquiry2 = new TreeMap<Integer, String>();
		field_inquiry2.put(0, "parameter");
		field_inquiry2.put(1, "value");

		results_inquiry2=connDb.getQuery("SELECT parameter, value FROM parameter WHERE parameter IN ('ImageDirectory','MinimumTweet','MinimumUserCreated','RegisterKeyword', 'RegisterRegex', 'MSISDNRegisterRegexIndex', 'CityRegisterRegexIndex', 'NameRegisterRegexIndex')", new Object[]{"",""}, field_inquiry2, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				if(results_inquiry2[i][0].toString().compareTo("RegisterKeyword")==0)
				{
					RegisterKeyword = results_inquiry2[i][1].toString();
				}
				if(results_inquiry2[i][0].toString().compareTo("RegisterRegex")==0)
				{
					RegisterRegex = results_inquiry2[i][1].toString();					
				}
				if(results_inquiry2[i][0].toString().compareTo("MSISDNRegisterRegexIndex")==0)
				{
					MSISDNRegisterRegexIndex = new Integer(results_inquiry2[i][1].toString()).intValue();					
				}
				if(results_inquiry2[i][0].toString().compareTo("CityRegisterRegexIndex")==0)
				{
					CityRegisterRegexIndex = new Integer(results_inquiry2[i][1].toString()).intValue();					
				}
				if(results_inquiry2[i][0].toString().compareTo("NameRegisterRegexIndex")==0)
				{
					NameRegisterRegexIndex = new Integer(results_inquiry2[i][1].toString()).intValue();					
				}
				if(results_inquiry2[i][0].toString().compareTo("MinimumTweet")==0)
				{
					minimumTweet = new Integer(results_inquiry2[i][1].toString()).intValue();					
				}
				if(results_inquiry2[i][0].toString().compareTo("MinimumUserCreated")==0)
				{
					minimumUserCreated = new Integer(results_inquiry2[i][1].toString()).intValue();					
				}
				if(results_inquiry2[i][0].toString().compareTo("ImageDirectory")==0)
				{
					imageDirectory = results_inquiry2[i][1].toString();					
				}
				
			}	
		}
		
	}

	public void getBid()
	{
		field_inquiry5 = new TreeMap<Integer, String>();
		field_inquiry5.put(0, "parameter");
		field_inquiry5.put(1, "value");

		results_inquiry5=connDb.getQuery("SELECT parameter, value FROM parameter WHERE parameter IN ('BidRegex', 'NominalBidRegexIndex')", new Object[]{"",""}, field_inquiry5, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				if(results_inquiry5[i][0].toString().compareTo("BidRegex")==0)
				{
					BidRegex = results_inquiry5[i][1].toString();	
				}
				if(results_inquiry5[i][0].toString().compareTo("NominalBidRegexIndex")==0)
				{
					NominalBidRegexIndex = new Integer(results_inquiry5[i][1].toString()).intValue();					
				}
			}	
		}
		
	}

	public void getUpdate()
	{
		field_inquiry3 = new TreeMap<Integer, String>();
		field_inquiry3.put(0, "parameter");
		field_inquiry3.put(1, "value");


		results_inquiry3=connDb.getQuery("SELECT parameter, value FROM parameter WHERE parameter IN ('UpdateKeyword', 'UpdateRegex', 'MSISDNUpdateRegexIndex', 'CityUpdateRegexIndex', 'NameUpdateRegexIndex')", new Object[]{"",""}, field_inquiry3, new Object[]{},0);
		if(connDb.getRowCount(0)>0)
		{    		
			for(int i=0;i<connDb.getRowCount(0);i++)
			{
				if(results_inquiry3[i][0].toString().compareTo("UpdateKeyword")==0)
				{
					UpdateKeyword = results_inquiry3[i][1].toString();
				}
				if(results_inquiry3[i][0].toString().compareTo("UpdateRegex")==0)
				{
					UpdateRegex = results_inquiry3[i][1].toString();					
				}
				if(results_inquiry3[i][0].toString().compareTo("MSISDNUpdateRegexIndex")==0)
				{
					MSISDNUpdateRegexIndex = new Integer(results_inquiry3[i][1].toString()).intValue();					
				}
				if(results_inquiry3[i][0].toString().compareTo("CityUpdateRegexIndex")==0)
				{
					CityUpdateRegexIndex = new Integer(results_inquiry3[i][1].toString()).intValue();					
				}
				if(results_inquiry3[i][0].toString().compareTo("NameUpdateRegexIndex")==0)
				{
					NameUpdateRegexIndex = new Integer(results_inquiry3[i][1].toString()).intValue();					
				}
			}	
		}
		
	}
	
	public int checkFormat(String input)
	{
		int result = 0;
		input = input.trim();
		input = input.replaceAll("  ", " ");
		getHashTag(input);
		String temp_nominal;
		if(checkKeyword(input).compareTo(UpdateKeyword)==0)
		{
			UpdateRegex = UpdateRegex.replaceFirst("UpdateKeyword", UpdateKeyword);
			input = input.replaceFirst(hashTag, "");
			Pattern p = Pattern.compile(UpdateRegex);
			Matcher m = p.matcher(input);
			while (m.find()) 
			{	
				name=m.group(NameUpdateRegexIndex);
				msisdn=m.group(MSISDNUpdateRegexIndex);
				city=m.group(CityUpdateRegexIndex);
				result=1;
			}	
		}
		else if(checkKeyword(input).compareTo(RegisterKeyword)==0)
		{
			RegisterRegex = RegisterRegex.replaceFirst("RegisterKeyword", RegisterKeyword);
			input = input.replaceFirst(hashTag, "");
			Pattern p = Pattern.compile(RegisterRegex);
			Matcher m = p.matcher(input);
			while (m.find()) 
			{	
				name=m.group(NameRegisterRegexIndex);
				msisdn=m.group(MSISDNRegisterRegexIndex);
				city=m.group(CityRegisterRegexIndex);
				result=2;
			}	

		}
		else if(checkKeyword(input).compareTo("BID")==0)
		{
			input = input.replaceFirst(getHashTag(input), "");
			input = input.replaceFirst("#", "");
			Pattern p = Pattern.compile(BidRegex);
			Matcher m = p.matcher(input);
			while (m.find()) 
			{	
				temp_nominal= m.group(NominalBidRegexIndex);
				temp_nominal = temp_nominal.replaceAll("k", "000");
				temp_nominal = temp_nominal.replaceAll("K", "000");
				temp_nominal = temp_nominal.replaceAll("m", "000000");
				temp_nominal = temp_nominal.replaceAll("M", "000000");
				nominal = new Integer(temp_nominal).intValue();
				result=3;
			}	
		}
		return result;
	}
	
//	public String parse(String input, String username)
	public String parse()
	{
		getNotification();
		String response = "";
//		userName = username;
//		message = input;

		checkUser(userName);
		int result = 0;
		if(getHashTag(message).compareTo("")==0)
		{
			response = _WRONG_HASHTAG;
		}
		else 
		{
			if(checkKeyword(message).compareToIgnoreCase("")==0)
			{
				response = _WRONG_KEYWORD;
				
			}
			else
			{
				result = checkFormat(message);

				
				if(result==0 && keyword.compareTo("Register")==0)
				{
					response = _REG_WRONG_FORMAT;
				}
				else if(result==0 && keyword.compareTo("Update")==0)
				{
					response = _UPDATE_WRONG_FORMAT;					
				}
				else if(result==0 && keyword.compareTo("Bid")==0)
				{
					response = _BID_WRONG_FORMAT;					
				}
				else if(result==2 && userId==0)
				{
					//getUserProfile(username);
					RegisterUser();
					response = _REG_VALID_FORMAT;										
				}
				else if(result==2 && userId!=0)
				{
					response = _REG_USER_HAVE_REGISTERED;										
				}
				else if(result==1 && userId!=0)
				{
					//getUserProfile(username);
					UpdateUser();
					response = _UPDATE_VALID_FORMAT;										
				}
				else if(result==1 && userId==0)
				{
					response = _UPDATE_USER_NOT_REGISTERED;										
				}
				else if(result==3 && userId==0)
				{
					response = _BID_USER_NOT_REGISTERED;															
				}
				else if(result==3 && userId!=0 && userStatus==1)
				{
					if(nominal==buy_it_now && buy_it_now>0)
					{
						UpdateHashTag();
						response = _BUY_IT_NOW;																						
					}
					else if(nominal<=currentMinimum)
					{
						response = _OUT_BID;																						
					}
					else
					{	
						currentMinimum=nominal;
						UpdateHashTag();
						response = _VALID_NOMINAL_BID;										
					}	
				}
				else if(result==3 && userId!=0 && userStatus==2)
				{
					response = _BLACKLIST_USER;
				}
			}
		}	
		//postMessageTweet(response);
		connDb.updateQuery("update traffic set response=?, id_hashtag=?, id_bid=?, status=?, created_date=now() where id=?",new Object[]{response,hashTagId,bidId,"2",trafficId});

		return response;
	}
	
	public static void main(String[] args) 
	{
		
		TwitterBid tweet = new TwitterBid();
		//String input = "#FFS21 REG Faiz 08118201461 Bandung";
		//String input = "#FFS21 UPDATE Yudy 08118201461  Jakarta";
		//String input = "#FFS21 JAJAN 08118201461 Yudy Bandung";
		//String input = "#FFS2 125k";
		//String username = "yudhi_h_utama";
		//tweet.postMessageImageTweet("Test3",2);
		//tweet.postMessageTweet("Testing9");
		//System.out.println(tweet.parse(input, username));
		//tweet.getTweet();
		
		// TODO Auto-generated method stub

	}
	
	

}
