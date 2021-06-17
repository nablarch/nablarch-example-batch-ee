nablarch-example-batch-ee
===============
Nablarch Framework（nablarch-fw-batch-ee）のバッチExampleアプリケーションです。

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

* 賞与計算バッチ(DB→DB)
    * EMPLOYEEテーブルから社員情報を取得し、賞与を計算してBONUSテーブルに登録する、Chunkステップのバッチです。
* 郵便番号テーブルTRUNCATEバッチ
    * ZIP_CODE_DATAテーブル と ZIP_CODE_DATA_WORKテーブル のデータを削除する、Batchletステップのバッチです。

動作させる処理は、引数の\<batch-job名\>を変更することで選択できます。

 * \<batch-job名\>に「bonus-calculate」を指定すると、賞与計算バッチが実行されます。
 * \<batch-job名\>に「zip-code-truncate-table」を指定すると、郵便番号テーブルTRUNCATEバッチが実行されます。

### 5. DBの確認方法

1. https://www.h2database.com/html/download.html からH2をインストールしてください。  
  ※2021年4月時点の最新版Version1.4.200には[既知の問題](https://github.com/h2database/h2database/issues/2078)があり、このリポジトリに含まれるDBにアクセスできないため、Last Stable版であるVersion1.4.199をご利用ください。

2. {インストールフォルダ}/bin/h2.bat を実行してください(コマンドプロンプトが開く)。
  ※h2.bat実行中はExampleアプリケーションからDBへアクセスすることができないため、バッチを実行できません。

3. ブラウザから http://localhost:8082 を開き、以下の情報でH2コンソールにログインしてください。
   JDBC URLの{dbファイルのパス}には、`nablarch_example.mv.db`ファイルの格納ディレクトリまでのパスを指定してください。  
  JDBC URL：jdbc:h2:{dbファイルのパス}/nablarch_example  
  ユーザ名：NABLARCH_EXAMPLE  
  パスワード：NABLARCH_EXAMPLE  


| master | develop |
|:-----------|:------------|
|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-batch-ee.svg?branch=master)](https://travis-ci.org/nablarch/nablarch-example-batch-ee)|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-batch-ee.svg?branch=develop)](https://travis-ci.org/nablarch/nablarch-example-batch-ee)|
