package no.uio.inf5750.assignment2.controller;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hamcrest.core.IsInstanceOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import no.uio.inf5750.assignment2.custom.exception.CustomException;
import no.uio.inf5750.assignment2.messagedao.HibernateMessageDao;
import no.uio.inf5750.assignment2.messagedao.MessageDao;
import no.uio.inf5750.assignment2.model.Message;





@Controller
public class BaseController {
	

	////////////////////////////////////////
	// Declerations
	///////////////////////////////////////
	@Autowired
	private MessageDao messageDao;
    static Logger logger = Logger.getLogger(BaseController.class);

	
	

	////////////////////////////////////////
	// Static Operations
	///////////////////////////////////////
	
	// Mapping Root URL Request  e.g localhost:8080/Assignment2_zubaira/
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
			
        model.addAttribute("message", "Maven Web Prooject + Spring 3 MVC - welcome()");	
		return "index";
		
		
	}
	
	
	// Default Mapping Handler
	@RequestMapping
	public String defaultHanlder(ModelMap model) {
			
        model.addAttribute("message", "This Path does not exits ....");	
		return "index";
		
		
	}

	
	// URL Mapping to HOME_PAGE/welcome/{userName}
	@RequestMapping(value = "/welcome/{userName}" , method = RequestMethod.GET)
	public String welcomeName(@PathVariable String userName, ModelMap model) {
	
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - "); 
		model.addAttribute("userName", userName);
        return "index";
		
	}
	
	
	// URL Mapping to /hello/{userName}
	@RequestMapping(value = "/hello/{userName}", method = RequestMethod.GET)
	public String helloName(@PathVariable String userName, ModelMap model){
		
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - "); 
		model.addAttribute("userName", userName);
		return "hello";
		
	}
	
	
	
	////////////////////////////////////////
	// Database Operations
	///////////////////////////////////////
	
	@RequestMapping(value="/send", method = RequestMethod.GET)
    public String send(@RequestParam(value="content") String content, ModelMap model) {

            Message msg = new Message();
            msg.setContent(content);
            int id = messageDao.save(msg);
            model.addAttribute("message", "Message id of stored message=" + id);
            return "index";

    }
	
	
	@RequestMapping(value="/read", method = RequestMethod.GET)
    public String read(ModelMap model) {

            Message message = messageDao.getLast();
            if (message != null) {
                    model.addAttribute("message", "The last message was: "+message.getContent());
            }
            else {
                    model.addAttribute("message", "No message found");                        
            }

            //Spring uses InternalResourceViewResolver and return back index.jsp
            return "index";
    }
	
	
	
	 @RequestMapping(value="/read/{id}", method = RequestMethod.GET)
     public String readId(@PathVariable int id, ModelMap model) {
		 	 
		  	
			Message message = messageDao.get(id);
            if (message != null) {
                
            	model.addAttribute("message", "Message number "+id+" was: "+message.getContent());
            
            }
            else {
            	  
            	logger.error("Message record with id: " + id + " Not Found ...");
              	throw	new CustomException(404,"Message record with id: " + id + " Not Found ...");
                    //model.addAttribute("message", "No message found");                        
            }
         //Spring uses InternalResourceViewResolver and return back index.jsp
         return "index";
     }
	 
	 
	 // Show all messsages 
	 @RequestMapping(value = "/getallmessages", method = RequestMethod.GET)
	 public String getAllMessages(ModelMap model) {
		 
		 List<Message> messageList = messageDao.getAllMessages();
		 if (messageList != null && !messageList.isEmpty()){
		
			 model.addAttribute("messageArray", messageList);
			 model.addAttribute("message",	" List of Messages Shown Below ");
			// System.out.println("DEBUG: Stage1 ");
			 
			 
		 }else {
			 
			 model.addAttribute("message",	" No Message Found in List ");
			 
		 }
		 
		 return "messageList";
		 
		
	}
	 
	 

	 ////////////////////////////////////////
	 // Exception Handling 
	 ///////////////////////////////////////
	 
		@ExceptionHandler(CustomException.class)
		public ModelAndView handleCustomException(CustomException ex) {

			ModelAndView model = new ModelAndView("errorpages/error");
			model.addObject("errCode", ex.getErrorCode());
			model.addObject("errMsg", ex.getErrorMessage());

			return model;

		}
		
		@ExceptionHandler(Exception.class)
		public ModelAndView handleCustomException(Exception ex) {

			ModelAndView model = new ModelAndView("errorpages/error");
			model.addObject("errCode", ex.getMessage());
			model.addObject("errMsg", ex.toString());

			return model;

		}
	
	

}

