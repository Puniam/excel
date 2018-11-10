<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<html>
<!DOCTYPE html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.bootcss.com/twitter-bootstrap/4.1.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/bootstrap-table/1.12.1/bootstrap-table.css" rel="stylesheet">

    <title>Title</title>
</head>
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.js"></script>
<script src="https://cdn.bootcss.com/twitter-bootstrap/4.1.3/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap-table/1.12.1/bootstrap-table.js"></script>
<script type="text/javascript" src="static/bootstrap-table-zh-CN.js"></script>
<script type="text/javascript" src="static/laydate/laydate.js"></script>

<body>
<div>
    <table id="table"></table>
</div>
<div style="margin-bottom: 20px; text-align: center">
    begin:<input type="text" id="beginTime">
    end:<input type="text" id="endTime">
    stockId:<input type="text" id="stockIdText">
</div>
<div>
<%--<form id="form" action="/upload" method="post" enctype="multipart/form-data" style="Float:left;height:30px">
    <input type="file" name="file">
</form>--%>
    <input id="file" type="file" name="file">
    <button class="btn btn-primary" id="import"  style="margin-left:30px;">导入</button>
    <button class="btn btn-primary" id="query"  style="margin-left:30px;">查询</button>
    <button class="btn btn-primary" id="delete" style="margin-left:30px;">删除</button>
    <button class="btn btn-primary" id="update" style="margin-left:30px;">更新</button>
    <button class="btn btn-primary" id="exportpdf" style="margin-left:30px;">导出pdf</button>
    <button class="btn btn-primary" id="exportexcel" style="margin-left:30px;">导出excel</button>
</div>
</body>
<script>
    $(function () {


        laydate.render({
            elem: '#beginTime' //指定元素
        });
        laydate.render({
            elem: '#endTime' //指定元素
        });

        //1.初始化Table
        var oTable = new TableInit();
        oTable.Init();

        //import
        $('#import').on('click',function () {
            var fileObj = document.getElementById("file").files[0];
            if (typeof (fileObj) == "undefined" || fileObj.size <= 0){
                alert("请选择文件");
                return;
            }
            var formFile = new FormData();
            formFile.append("file", fileObj); //加入文件对象

            $.ajax({
                url: "/upload",
                data: formFile,
                type: "post",
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (result) {
                    alert("上传完成!");
                },
            })

        })
        
        //按条件搜索
        $('#query').on('click', function () {

            $('#table').bootstrapTable('refresh');

        });
        //按条件删除
        $('#delete').on('click', function () {

            var beginTime = $('#beginTime').val();
            var endTime = $('#endTime').val();
            var stockId = $('#stockIdText').val();
            $.ajax({
                type: 'post',
                url: '/del',
                data: {beginTime: beginTime, endTime: endTime, stockId: stockId},
                success: function (data) {
                    alert("del_success");
                    $('#table').bootstrapTable('refresh');
                },
                error: function (data) {
                    alert("error");
                }
            });


        });

        //按条件跟新 辣鸡实现方法
        $('#update').on('click', function () {

            var beginTime = $('#beginTime').val();
            var endTime = $('#endTime').val();
            var stockId = $('#stockIdText').val();
            $.ajax({
                type: 'post',
                url: '#',
                data: {beginTime: beginTime, endTime: endTime, stockId: stockId},
                success: function (data) {
                    alert("能力不足无法完成")
                },
                error: function (data) {
                    alert("能力不足无法完成")
                }

            });
        });


        //pdf导出
        $('#exportpdf').on('click', function () {

            var beginTime = $('#beginTime').val();
            var endTime = $('#endTime').val();
            var stockId = $('#stockIdText').val();
            $.ajax({
                type: 'post',
                url: '/exportPdf',
                data: {beginTime: beginTime, endTime: endTime, stockId: stockId},
                success: function (data) {
                    var url="http://localhost:8080/downloadPdf?fileName="+data;
                    window.open(url);
                    console.log(url);
                    console.log(data)
                },
                error: function (data) {
                    console.log("失败"+data)
                }
            });

        });

        //excel 导出
        $('#exportexcel').on('click', function () {
            alert("excel")
            var beginTime = $('#beginTime').val();
            var endTime = $('#endTime').val();
            var stockId = $('#stockIdText').val();
            var url="http://localhost:8080/downloadExcel?beginTime="+beginTime
                +"&endTime="+endTime+"&stockId="+stockId;
            window.open(url);

        });

    });





    var TableInit = function(){
        var oTableInit = new Object();
        oTableInit.Init = function () {
            $('#table').bootstrapTable({
                url: '/data',
                method:'get',
                dataField: "data",
                contentType:'application/json',
                dataType:'json',
                pageSize:10,
                pageNumber:1,
                sidePagination: "server",
                smartDisplay:false,
                height: 483,
                queryParams: oTableInit.queryParams,
                showPaginationSwitch:false,
                pagination: true,                  //是否显示分页（*）
                columns: [{
                    field: 'thedate',
                    title: 'TheDate',
                    width:180
                }, {
                    field: 'stockid',
                    title: 'StockId'
                }, {
                    field: 'openprice',
                    title: 'OpenPrice'
                }, {
                    field: 'highprice',
                    title: 'HighPrice'
                }, {
                    field: 'lowprice',
                    title: 'LowPrice'
                }, {
                    field: 'closeprice',
                    title: 'ClosePrice'
                }, {
                    field: 'matchqty',
                    title: 'MatchQty'
                }, {
                    field: 'matchvalue',
                    title: 'MatchValue'
                },]
            });
        };
        //得到查询的参数
        oTableInit.queryParams = function (params) {
            console.log(params);
            console.log(params.offset);
            console.log(params.limit);
            var beginTime=$('#beginTime').val();
            var endTime=$('#endTime').val();
            var stockId=$('#stockIdText').val();
            console.log(beginTime+" "+endTime);
            var temp = {

                //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
                pageSize: params.limit,   //页面大小
                pageIndex: params.offset/params.limit+1, //页码
                stockId:stockId,
                beginTime:beginTime,
                endTime:endTime,
            };
            return temp;
        };

        return oTableInit;
    }


</script>
</html>
