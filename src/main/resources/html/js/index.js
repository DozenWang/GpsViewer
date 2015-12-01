$(function () {

    var map,
        lineArr = new Array();

    map = new AMap.Map("mapContainer", {
        resizeEnable: true,
        //二维地图显示视口
        view: new AMap.View2D({
            //地图中心点
            center: new AMap.LngLat(116.397428, 39.90923),
            //地图显示的缩放级别
            zoom: 18
        })
    });
    // 读取配置文件
    //if (gConf) {
    //    userConf.labelType = gConf.labelType;
    //}

    (function loadLocations() {
        var data = gLocations;
        // 加载GPS点
        var arr_x = [];
        var arr_y = [];
        var arr_r = [];
        var arr_time = [];

        $.each(data, function (infoIndex, info) {
            arr_x.push(info["longitude"]);
            arr_y.push(info["latitude"]);
            arr_r.push(info["accuracy"]);
            arr_time.push(info["time"]);
        });

        context = {
            loc_x: arr_x,
            loc_y: arr_y,
            loc_r: arr_r,
            loc_time: arr_time
        };
        AMap.event.addListener(map, "complete", completeEventHandler, context);
    })();

    function drawGpsLine(lineArr) {
        //绘制轨迹
        var polyline = new AMap.Polyline({
            map: map,
            path: lineArr, //设置线覆盖物路径
            strokeColor: "#3366FF", //线颜色
            strokeOpacity: 1, //线透明度
            strokeWeight: 5, //线宽
            strokeStyle: "solid", //线样式
            strokeDasharray: [10, 5] //补充线样式
        });
        map.setFitView();
    }

    function addCircle(x, y, r) {

        var fcolor;
        var fopacity;

        fcolor = "#ee2200";
        fopacity = 0.15;

        circle = new AMap.Circle({
            center: new AMap.LngLat(x, y), // 圆心位置
            radius: r, //半径
            strokeColor: "#F33", //线颜色
            strokeOpacity: 0.3, //线透明度
            strokeWeight: 4, //线粗细度
            fillColor: fcolor, //填充颜色
            fillOpacity: fopacity //填充透明度
        });
        circle.setMap(map);
    }

    function addLabel(lon, lat, label) {
        var marker = new AMap.Marker({
            position: new AMap.LngLat(lon, lat)
        });

        // 设置label标签
        marker.setLabel({ //label的父div默认蓝框白底右下角显示，样式className为：amap-marker-label
            //offset:new AMap.Pixel(50,50),//修改父div相对于maker的位置
            content: label,
            title: label
        });
        marker.on('click', function () {
            alert(label)
        });
        marker.setMap(map);
    }

    //地图图块加载完毕后执行函数
    function completeEventHandler() {
        var arr_x = this.loc_x,
            arr_y = this.loc_y,
            arr_r = this.loc_r,
            arr_time = this.loc_time,
            lineArr = new Array();

        for (var i = 0; i < arr_y.length; i++) {
            lineArr.push(new AMap.LngLat(arr_x[i], arr_y[i]));
        }

        drawGpsLine(lineArr);
        var time = new Date();
        for (var i = 0; i < arr_r.length; i++) {
            addCircle(arr_x[i], arr_y[i], arr_r[i]);
            if (gConf) {
                var offset = gConf.index || 1;
                if (gConf.labelType == 'time') {
                    time.setTime(arr_time[i]);
                    addLabel(arr_x[i], arr_y[i], time.toLocaleString());
                } else if (gConf.labelType == 'num') {
                    addLabel(arr_x[i], arr_y[i], i + offset);
                }
            }

        }
    }

    $("#start").click(function () {
        marker.moveAlong(lineArr, 500);
    });

    $("#stop").click(function () {
        marker.stopMove();
    });

});