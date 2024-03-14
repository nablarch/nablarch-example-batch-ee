nablarch-example-batch-ee
===============
Nablarch Framework（nablarch-fw-batch-ee、nablarch-etl）のバッチExampleアプリケーションです。

## 実行手順

### 1.動作環境
実行環境に以下のソフトウェアがインストールされている事を前提とします。
* Java Version : 8
* Maven 3.0.5以降

なお、このアプリケーションはH2 Database Engineを組み込んでいます。別途DBサーバのインストールは必要ありません。

### 2. プロジェクトリポジトリの取得
Gitを使用している場合、アプリケーションを配置したいディレクトリにて「git clone」コマンドを実行してください。
以下、コマンドの例です。

    $mkdir c:\example
    $cd c:\example
    $git clone https://github.com/nablarch/nablarch-example-batch-ee.git

Gitを使用しない場合、最新のタグからzipをダウンロードし、任意のディレクトリへ展開してください。

### 3. アプリケーションのビルド

#### 3.1. データベースのセットアップ及びエンティティクラスの作成
まず、データベースのセットアップ及びエンティティクラスの作成を行います。以下のコマンドを実行してください。

    $cd nablarch-example-batch-ee
    $mvn generate-resources

実行に成功すると、以下のようなログがコンソールに出力され、nablarch-example-batchディレクトリの下にtargetディレクトリが作成されます。

    (中略)
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    (中略)

#### 3.2. アプリケーションのビルド

次に、アプリケーションをビルドします。以下のコマンドを実行してください。

    $mvn clean package

実行に成功すると、以下のようなログがコンソールに出力されます。

    (中略)
    [INFO]
    [INFO] --- maven-jar-plugin:2.5:jar (default-jar) @ nablarch-example-batch-ee ---
    [INFO] Building jar: c:\example\nablarch-example-batch-ee\target\nablarch-example-batch-ee-1.0.1.jar
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    (中略)


### 4. アプリケーションの実行

チェックアウトディレクトリにて以下のコマンドを実行すると、サンプルアプリケーションを動作させることができます。

```
    $mvn exec:java -Dexec.mainClass=nablarch.fw.batch.ee.Main -Dexec.args=<batch-job名>
```

\<batch-job名\>の指定例を示します。

```
    $mvn exec:java -Dexec.mainClass=nablarch.fw.batch.ee.Main -Dexec.args=zip-code-truncate-table
```

なお、 `maven-assembly-plugin` を使用して実行可能jarの生成を行っているため、以下の手順にて実行することもできる。

1. ``target/application-<version_no>.zip`` を任意のディレクトリに解凍する。
2. 以下のコマンドにて実行する

  ```
      java -jar <1で解凍したディレクトリ名>/nablarch-example-batch-ee-<version_no>.jar <batch-job名>
  ```


\<batch-job名\>を変えることで、CSVからDBおよびDBからCSVへのデータ保存と、DBのTRUNCATE処理を行うことができます。
実行後、次のCSVファイルやテーブルを見て処理結果を確認してください。

動作させることができる処理は、次の通りです。

* JBatchのみ利用（ETLなし）
    * 賞与計算バッチ(DB→DB)
        * EMPLOYEEテーブルから社員情報を取得し、賞与を計算してBONUSテーブルに登録する、Chunkステップのバッチです。
    * 郵便番号テーブルTRUNCATEバッチ
        * ZIP_CODE_DATAテーブル と ZIP_CODE_DATA_WORKテーブル のデータを削除する、Batchletステップのバッチです。
* ETLとJBatchを利用
    * 郵便番号登録ETLバッチ(SQL*LoaderによるCSV→DB)
        * \<チェックアウトディレクトリ\>/testdata/input/KEN_ALL.CSV を入力元とし、SQL*Loaderにより ZIP_CODE_DATA テーブルにデータを登録します。
        * __SQL*Loaderを使用するため、実行する際はデータベースをOracleに変更してください。__ 変更方法は[DBの変更について](#6-DBの変更について)を見てください。
        * SQL\*Loaderでエラーが出た場合、testdata/sqlloader/output/にSQL\*Loaderのログが出力されるため、確認してください。
    * 郵便番号登録ETLバッチ(SQL*Loaderを使わないCSV→DB)
        * \<チェックアウトディレクトリ\>/testdata/input/KEN_ALL.CSV を入力元とし、ZIP_CODE_DATA テーブルにデータを登録します。
        * SQL*Loaderは使いません。
    * 郵便番号出力バッチ(DB→CSV)
        * ZIP_CODE_DATAテーブルのデータを \<チェックアウトディレクトリ\>/testdata/output 以下に出力します。
    * 郵便番号登録ETLバッチ(固定長→DB)
        * \<チェックアウトディレクトリ\>/testdata/input/fixedlength-zip-code-data を入力元とし、ZIP_CODE_DATA テーブルにデータを登録します。
        * SQL*Loaderは使いません。
    * 郵便番号出力バッチ(DB→固定長)
        * ZIP_CODE_DATAテーブルのデータを \<チェックアウトディレクトリ\>/testdata/output 以下に出力します。

動作させる処理は、引数の\<batch-job名\>を変更することで選択できます。

* JBatchのみ利用（ETLなし）
    * \<batch-job名\>に「bonus-calculate」を指定すると、賞与計算バッチが実行されます。
    * \<batch-job名\>に「zip-code-truncate-table」を指定すると、郵便番号テーブルTRUNCATEバッチが実行されます。
* ETLとJBatchを利用
    * \<batch-job名\>に「etl-zip-code-csv-to-db-insert-batchlet」を指定すると、郵便番号登録ETLバッチ(SQL*LoaderによるCSV→DB)が実行されます。
    * \<batch-job名\>に「etl-zip-code-csv-to-db-chunk」を指定すると、郵便番号登録ETLバッチ(SQL*Loaderを使わないCSV→DB)が実行されます。
    * \<batch-job名\>に「etl-zip-code-db-to-csv-chunk」を指定すると、郵便番号出力バッチ(DB→CSV)が実行されます。
    * \<batch-job名\>に「etl-zip-code-fixedlength-to-db-chunk」を指定すると、郵便番号登録ETLバッチ(固定長→DB)が実行されます。
    * \<batch-job名\>に「etl-zip-code-db-to-fixedlength-chunk」を指定すると、郵便番号出力バッチ(DB→固定長)が実行されます。


なお、インプットのCSV、固定長データは、下記サイトより取得できる郵便番号データ（全国一括）を元にしています。

* http://www.post.japanpost.jp/zipcode/dl/kogaki-zip.html

以下の項目が入っています。
* 全国地方公共団体コード（JIS X0401、X0402）
* （旧）郵便番号（5桁）
* 郵便番号（7桁）
* 都道府県名　半角カタカナ
* 市区町村名　半角カタカナ
* 町域名　半角カタカナ
* 都道府県名　漢字
* 市区町村名　漢字
* 町域名　漢字
* 一町域が二以上の郵便番号で表される場合の表示　（「1」は該当、「0」は該当せず）
* 小字毎に番地が起番されている町域の表示　（「1」は該当、「0」は該当せず）
* 丁目を有する町域の場合の表示　（「1」は該当、「0」は該当せず）
* 一つの郵便番号で二以上の町域を表す場合の表示　（「1」は該当、「0」は該当せず）
* 更新の表示　（「0」は変更なし、「1」は変更あり、「2」廃止（廃止データのみ使用））
* 変更理由　（「0」は変更なし、「1」市政・区政・町政・分区・政令指定都市施行、「2」住居表示の実施、 「3」区画整理、「4」郵便区調整等、「5」訂正、「6」廃止（廃止データのみ使用））

### 5. DBの確認方法

1. https://www.h2database.com/html/download.html からH2をインストールしてください。  

2. {インストールフォルダ}/bin/h2.bat を実行してください(コマンドプロンプトが開く)。
  ※h2.bat実行中はExampleアプリケーションからDBへアクセスすることができないため、バッチを実行できません。

3. ブラウザから http://localhost:8082 を開き、以下の情報でH2コンソールにログインしてください。
   JDBC URLの{dbファイルのパス}には、`nablarch_example.mv.db`ファイルの格納ディレクトリまでのパスを指定してください。  
  JDBC URL：jdbc:h2:{dbファイルのパス}/nablarch_example  
  ユーザ名：NABLARCH_EXAMPLE  
  パスワード：NABLARCH_EXAMPLE  

### 6. DBの変更について

DBをOracleに変更する方法に関しては、解説書の以下の章を参照してください。

* 8.6.2 gsp-dba-maven-plugin(DBA作業支援ツール)の初期設定方法
* 8.6.1 使用するRDBMSの変更手順

ただし、gsp-dba-maven-pluginの設定に関して、data-model.edmの準備の手順の代わりに、以下の手順を行ってください。

    pom.xmlのpropertiesタグにある、dba.erdFileの値を以下の値に書き換える
    src/main/resources/entity/oracle.edm


| master | develop |
|:-----------|:------------|
|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-batch-ee.svg?branch=master)](https://travis-ci.org/nablarch/nablarch-example-batch-ee)|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-batch-ee.svg?branch=develop)](https://travis-ci.org/nablarch/nablarch-example-batch-ee)|
