package common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	@Autowired
    private RedisService redisService;
        	
    public boolean checkToken(HttpServletRequest request) throws Exception {

        String token = request.getHeader("token");
        if (null == token || token.isEmpty()) {
           System.out.println("UID missing");
           return false;
        }

        if (redisService.exists(token)) {
            System.out.println("Duplicate request");
            return false;
        }

        boolean remove = redisService.remove(token);
        if (!remove) {
            System.out.println("Duplicate request");
            return false;
        }
        
        redisService.setEx(token, token,1000L);
        return true;
    }
}