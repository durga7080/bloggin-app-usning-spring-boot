package com.blog.blogapp.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtHealper {
	
	public static final long JWT_TOKEN_VALIDITY = 5*60*60;
	
	private String secret ="jwtTokenKey";
	
	//retrive username from token
	public String getUsernameFromToken(String token) {
		
		return getClaimsFromToken(token, Claims::getSubject);
	}
	
	//retrive Expiration date from jwt token
	public Date getExpirationDateFromToken(String token)
	{
		return getClaimsFromToken(token,Claims::getExpiration);
	}
	
	public <T> T getClaimsFromToken(String token,Function<Claims, T> claimsResolver)
	{
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	//for retriving any information from token we wil need the secret key
	private Claims getAllClaimsFromToken(String token)
	{
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	//check if the token has expired
	
	private Boolean isTokenExpired(String token)
	{
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	//generate token for user
	
	public String generateToken(UserDetails userDetails)
	{
		Map<String,Object> claims = new HashMap<>();
		return doGenerateToken(claims,userDetails.getUsername());
	}
	
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	//for validate token
	public Boolean validateToken(String token,UserDetails userDetails)
	{
		final String username = getUsernameFromToken(token);
		System.out.print("username is"+username);
		return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
	}

}
