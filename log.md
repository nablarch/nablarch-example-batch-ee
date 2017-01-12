# 終了ステータスについて

運用担当者はバッチの終了ステータスを監視する。  
終了ステータスを見て、詳細な情報が必要な場合はログを参照する。

## 終了ステータスの種類
* 正常  
  バッチが正常に終了したことを意味する。

* 異常  
  バッチが何らかの理由で終了できなかったことを意味する。  
  運用担当者はログを参照し、障害対応を行う必要がある。

* 警告  
  バッチは終了したが、正常とは異なる事象が発生したことを意味する。  
  運用担当者はログを参照し、障害対応を行う必要がある。

# ログについて

## ログの種類
* all.log  
  `progress.log`の内容を除く全てのログが出力されるログ。  
  `operation.log` に対応した詳細情報(スタックトレース)は、このログに出力される。
  
* operation.log  
  障害発生時のメッセージが出力されるログ。  
  運用担当者が、この情報を元に障害対応を行う。(運用担当者が対処すべきログは、`operator`と出力される)
  システムエラーの場合には保守担当者に連絡を行う。

* progress.log  
  進捗状況を表すログ。  
  処理遅延時などは、このログを元に現在の処理の終了予測を行い、続行や停止の判断を行う事ができる

## 障害時のログ参照のイメージ
![](images/log.png)
