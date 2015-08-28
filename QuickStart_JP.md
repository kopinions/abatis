設定<p>

<img src='http://abatis.android-fun.jp/abatis_01.png' />

<BR>

   図-1<br>
<blockquote>valuesの下にsqlmaps.xml(ファイル名は任意)を作成する。（図-1参照）<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
</blockquote>


<img src='http://abatis.android-fun.jp/abatis_02.png' />

<BR>

   図-2<br>
<br>
<BR><br>
<br>
<br>
nameをinitializeし、sqlを登録する。<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
DB fileが存在しない場合（アプリ初回起動時）DBファイルを<br>
<br>
<BR><br>
<br>
<br>
引数のDB名を利用して生成し、 このsqlでdb初期化が行われる。<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
データ取得(select)<br>
<br>
<BR><br>
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
<br>
<img src='http://abatis.android-fun.jp/abatis_03.png' />

<BR>

   図-3<br>
<br>
<BR><br>
<br>
<br>
nameにsqlidを指定してselect文を登録する。bindする値は'#'で囲む。<br>
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
<img src='http://abatis.android-fun.jp/abatis_04.png' />

<BR>

   図-4<br>
<br>
<BR><br>
<br>
<br>
abatisインスタンスを取得し、parameter（map）とsqlidを渡して結果listを得る。<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
DB file名を省略した場合はdefault（database.db）名で生成される。<br>
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
<br>
例） <code>AbatisService abatisService = AbatisService.getInstance(getApplicationContext());</code>

<BR>

<br>
<br>
<BR><br>
<br>
<br>
<br>
<br>
データ更新<code>(insert, update, delete)</code>

<BR>

<br>
<br>
<BR><br>
<br>
<br>
<img src='http://abatis.android-fun.jp/abatis_05.png' />

<BR>

   図-5<br>
<br>
<BR><br>
<br>
<br>
nameにsqlidを指定してinsertおよびupdate文を登録する。bindする値は'#'で囲む。<br>
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

   図-6<br>
<br>
<BR><br>
<br>
<br>
abatisインスタンスを取得し、parameter（map）とsqlidを渡して実行する。<br>
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
DB file名を省略した場合はdefault（database.db）名で生成される。<br>
<br>
<BR><br>
<br>
<br>
<br>
<BR><br>
<br>
<br>
<br>
<blockquote>例） <code>AbatisService abatisService =AbatisService.getInstance(getApplicationContext());</code>

<BR>

<br>
<br>
<BR><br>
<br>
