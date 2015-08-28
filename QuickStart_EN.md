Getting stared with aBatis<p>

<img src='http://abatis.android-fun.jp/abatis_01.png' />

<BR>

 figure-1<br>
<br>
<BR><br>
<br>
 Inside the res/values/ folder, create a new xml file(e.g. sqlmaps.xml). （see figure-1）<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<img src='http://abatis.android-fun.jp/abatis_02.png' />

<BR>

 figure-2<br>
<br>
<BR><br>
<br>
<br>
In the new xml file, add new string and name "initialize".<br>
<br>
<BR><br>
<br>
<br>
Compose Create Table SQL statement as a string resource.<br>
<br>
<BR><br>
<br>
<br>
<br>
initialize with the SQL statement during first start-up or<br>
<br>
<BR><br>
<br>
<br>
when you create new DB.<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<h3>getting data.(select)</h3>
<img src='http://abatis.android-fun.jp/abatis_04.png' />

<BR>

 figure-3<br>
<br>
<BR><br>
<br>
<br>
In the xml file, add new string and name "sqlId".<br>
<br>
<BR><br>
<br>
<br>
Compose Select SQL statement as a string resource.<br>
<br>
<BR><br>
<br>
<br>
Bind variables should be bracketed with #.<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
<h3>Updating data(insert, update, delete)</h3>

<BR>

<br>
<br>
<BR><br>
<br>
<br>
<img src='http://abatis.android-fun.jp/abatis_03.png' />

<BR>

 figure-4<br>
<br>
<BR><br>
<br>
<br>
get aBatis instance and pass parameter（map）and sqlId as an argument to DB.<br>
<br>
<BR><br>
<br>
<br>
Then return result as a list type.<br>
<br>
<BR><br>
<br>
<br>
When DB file name is omitted, complemented by default name(database.db).<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<code>　e.g.） AbatisService abatisService = AbatisService.getInstance(getApplicationContext());</code>

<BR>

<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
<img src='http://abatis.android-fun.jp/abatis_05.png' />

<BR>

figure-5<br>
<br>
<BR><br>
<br>
<br>
In the new xml file, add new string and name "sqlId".<br>
<br>
<BR><br>
<br>
<br>
Compose insert/update/delete SQL statement as a string resource.<br>
<br>
<BR><br>
<br>
<br>
Bind variables should be bracketed with #.<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<img src='http://abatis.android-fun.jp/abatis_06.png' />

<BR>

figure-6<br>
<br>
<BR><br>
<br>
<br>
get aBatis instance and pass parameter（map）and sqlId as an argument to DB.<br>
<br>
<BR><br>
<br>
<br>
Then return result as a list type.<br>
<br>
<BR><br>
<br>
<br>
When DB file name is omitted, complemented by default name(database.db).<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<code>例） AbatisService abatisService =AbatisService.getInstance(getApplicationContext());</code>

<BR>

<br>
<br>
<BR><br>
<br>
