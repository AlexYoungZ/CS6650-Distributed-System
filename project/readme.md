I implement the communicate protocol at server side since normally application won't limit what client request would be like(make it easy for client to use), instead let server decide if client request is valid or not.    

Operations should be matched in this format:
1) PUT (key, value) 
2) GET (key)
3) DELETE(key)

TCP ServerLog example:
