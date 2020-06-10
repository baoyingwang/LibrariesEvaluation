[TOC]
# Overview
- 这个文件夹是启动docker compose的地方
- 基本使用方法
  - docker-compose up 启动
  - docker-compose down 停止
  - docker-compose rm 删除container - image是提前build好的（mvn docker:build)
- note: container的名称有profile前缀，譬如dev_evalspringservice_1，prod_evalspringservice_1
