package fr.oxygames.app.core.server

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object DataBase {
    @JvmStatic
    fun connection(){
        try {
            val con = DriverManager.getConnection(
                "jdbc:mysql://oxygames.fr/db_wordpress?serverTimezone=UTC",
                "oxyman",
                "BBR97136KELYAN"
            )
            println("OK !")
            val s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
            val users = s.executeQuery("SELECT 'ID','user_login','user_pass','user_nicename','user_email','user_url','user_registered','user_activation_key','user_status','display_name' FROM 'oxy_users'")

        }
        catch (e: SQLException){
            e.printStackTrace()
        }
    }
    fun posts(){
        try {
            val conn = DriverManager.getConnection(
                "jdbc:mysql://oxygames.fr/db_wordpress?",
                "oxy",
                "BBR97136KELYAN"
            )
            println("OK !")
            val postData = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
            val post = postData.executeQuery("SELECT 'ID','post_author','post_date','post_title','post_content','post_status','post_name','post_type' FROM 'oxy_posts'")
            val rm = post.metaData

            rm.getCatalogName(1)

            post.next()
            val postTitle = post.getString("post_title")
            println(postTitle)
        }
        catch (e: SQLException){
            e.printStackTrace()

        }
    }
}