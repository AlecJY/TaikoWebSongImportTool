package com.alebit.taikoweb

import com.alebit.taikoweb.struct.CsrfToken
import com.alebit.taikoweb.struct.Login
import com.alebit.taikoweb.struct.LoginResponse
import com.alebit.taikoweb.struct.TaikoWebSong
import com.google.gson.Gson
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.jsoup.Jsoup
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class TaikoWeb {
    private val baseUri: URI;
    private val httpClient: HttpClient;
    private val gson = Gson();

    constructor(baseUri: String, username: String, password: String) {
        this.baseUri = URI.create(baseUri);
        this.httpClient = HttpClientBuilder.create()
            .setDefaultCookieStore(BasicCookieStore())
            .build();
        val result = login(username, password);
        if (!result) {
            throw Exception("Login error");
        }
    }

    private fun login(username: String, password: String): Boolean {
        val token = getCsrfToken();
        if (token == null) {
            println("Could not contact with the server");
            return false;
        }
        val login = Login(username = username, password = password, remember = false);
        val loginURI = baseUri.resolve("/api/login");
        val request = HttpPost(loginURI);
        request.addHeader("x-csrftoken", token);
        request.entity = StringEntity(gson.toJson(login), ContentType.APPLICATION_JSON);
        val response = httpClient.execute(request);
        val reader = InputStreamReader(response.entity.content);
        val result = gson.fromJson(reader, LoginResponse::class.java);
        request.releaseConnection();
        if (result.status.equals("ok")) {
            println("Login as \"" + result.displayName + "\"");
            return true;
        }
        println("Fail to login");
        return false;
    }

    private fun getCsrfToken(): String? {
        val csrfURI = baseUri.resolve("/api/csrftoken");
        val request = HttpGet(csrfURI);
        val response = httpClient.execute(request);
        val reader = InputStreamReader(response.entity.content);
        val token = gson.fromJson(reader, CsrfToken::class.java);
        request.releaseConnection();
        if (token.status.equals("ok")) {
            return token.token;
        }
        return null;
    }

    fun addSong(song: TaikoWebSong, tja: File, target: Path): Boolean {
        val songInfo = getSongInfo("new") ?: return false;

        if (songInfo.id <= 0) {
            return false;
        }
        val targetDir = target.resolve(songInfo.id.toString());
        if (!targetDir.toFile().exists()) {
            if (!targetDir.toFile().mkdirs()) {
                System.err.println("Copy song data error");
                return false;
            }
        } else if (!targetDir.toFile().isDirectory) {
            System.err.println("Copy song data error");
            return false;
        }
        try {
            Files.copy(tja.toPath(), targetDir.resolve("main.tja"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(song.musicFile!!.toPath(),
                targetDir.resolve(if (song.musicType.equals("ogg")) "main.ogg" else "main.mp3"),
                StandardCopyOption.REPLACE_EXISTING);
            if (song.lyrics) {
                Files.copy(song.lyricFile!!.toPath(), targetDir.resolve("main.vtt"),
                    StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (e: Exception) {
            System.err.println("Copy song data error");
            targetDir.toFile().deleteRecursively();
            return false;
        }

        val data = ArrayList<BasicNameValuePair>();
        data.add(BasicNameValuePair("csrf_token", songInfo.token));
        data.add(BasicNameValuePair("enabled", "on"));
        data.add(BasicNameValuePair("title", song.title));
        data.add(BasicNameValuePair("title_ja", song.titleJa ?: ""));
        data.add(BasicNameValuePair("title_en", song.titleEn ?: ""));
        data.add(BasicNameValuePair("title_cn", song.titleCn ?: ""));
        data.add(BasicNameValuePair("title_tw", song.titleTw ?: ""));
        data.add(BasicNameValuePair("title_ko", song.titleKo ?: ""));
        data.add(BasicNameValuePair("subtitle", song.subtitle ?: ""));
        data.add(BasicNameValuePair("subtitle_ja", song.subtitleJa ?: ""));
        data.add(BasicNameValuePair("subtitle_en", song.subtitleEn ?: ""));
        data.add(BasicNameValuePair("subtitle_cn", song.subtitleCn ?: ""));
        data.add(BasicNameValuePair("subtitle_tw", song.subtitleTw ?: ""));
        data.add(BasicNameValuePair("subtitle_Ko", song.subtitleKo ?: ""));
        data.add(BasicNameValuePair("course_easy", intToString(song.courseEasy)));
        if (song.branchEasy) data.add(BasicNameValuePair("branch_easy", "on"));
        data.add(BasicNameValuePair("course_normal", intToString(song.courseNormal)));
        if (song.branchNormal) data.add(BasicNameValuePair("branch_normal", "on"));
        data.add(BasicNameValuePair("course_hard", intToString(song.courseHard)));
        if (song.branchHard) data.add(BasicNameValuePair("branch_hard", "on"));
        data.add(BasicNameValuePair("course_oni", intToString(song.courseOni)));
        if (song.branchOni) data.add(BasicNameValuePair("branch_oni", "on"));
        data.add(BasicNameValuePair("course_ura", intToString(song.courseUra)));
        if (song.branchUra) data.add(BasicNameValuePair("branch_ura", "on"));
        data.add(BasicNameValuePair("category_id", song.category.value.toString()));
        data.add(BasicNameValuePair("type", song.chartType));
        data.add(BasicNameValuePair("music_type", song.musicType));
        data.add(BasicNameValuePair("offset", "0"));
        data.add(BasicNameValuePair("skin_id", "0"));
        data.add(BasicNameValuePair("preview", song.preview.toString()));
        data.add(BasicNameValuePair("volume", song.volume.toString()));
        data.add(BasicNameValuePair("maker_id", "0"));
        if (song.lyrics) data.add(BasicNameValuePair("lyrics", "on"));
        data.add(BasicNameValuePair("hash", ""));
        data.add(BasicNameValuePair("gen_hash", "on"));
        val addSongURI = baseUri.resolve("/admin/songs/new");
        val request = HttpPost(addSongURI);
        request.entity = UrlEncodedFormEntity(data, Charsets.UTF_8);
        val response = httpClient.execute(request);
        val status = response.statusLine.statusCode;
        request.releaseConnection();
        if (status == 302) {
            return true;
        }
        System.err.println("Server error " + response.statusLine.statusCode + ". Could not add song \"" + song.title + "\"");
        targetDir.toFile().deleteRecursively();
        return false;
    }

    private fun intToString(num: Int?): String {
        return if (num == null) "" else num.toString();
    }

    private data class SongInfo (
        val id: Int,
        val token: String,
    );

    private fun getSongInfo(songId: String): SongInfo? {
        val newSongURI = baseUri.resolve("/admin/songs/" + songId);
        val request = HttpGet(newSongURI);
        val response = httpClient.execute(request);
        val charset = ContentType.getOrDefault(response.entity).charset;
        val doc = Jsoup.parse(response.entity.content, charset.name(), newSongURI.toASCIIString());
        request.releaseConnection();
        val songIds = doc.body().getElementsByClass("song-id");
        if (songIds.size != 1) {
            System.err.println("Could not get song id");
            return null;
        }
        val rawId = songIds[0].text();
        try {
            val id = Integer.parseInt(rawId.substring(5, rawId.length - 1));
            val tokens = doc.getElementsByAttributeValue("name", "csrf_token");
            if (tokens.size != 1) {
                System.err.println("Could not get csrf token");
                return null;
            }
            if (!tokens[0].attributes().hasKey("value")) {
                System.err.println("Could not get csrf token");
                return null;
            }
            val token = tokens[0].attributes().get("value");
            return SongInfo(id, token);
        } catch (e: NumberFormatException) {
            System.err.println("Could not get song id");
        }
        return null;
    }

    fun deleteSong(id: Int) {
        val songInfo = getSongInfo(id.toString());
        if (songInfo != null) {
            val deleteSongURI = baseUri.resolve("/admin/songs/" + id + "/delete");
            val data = ArrayList<BasicNameValuePair>();
            data.add(BasicNameValuePair("csrf_token", songInfo.token));
            val request = HttpPost(deleteSongURI);
            httpClient.execute(request);
            request.releaseConnection();
        }
    }
}