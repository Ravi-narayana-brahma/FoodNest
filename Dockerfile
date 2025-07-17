FROM tomcat:9.0

# Expose the default Tomcat port
EXPOSE 8080

# Copy WAR file into Tomcat webapps directory
COPY FoodNest.war /usr/local/tomcat/webapps/
