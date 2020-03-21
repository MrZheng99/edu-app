var videos = document.getElementById("videos");
var rtc = SkyRTC();


//�ɹ�����WebSocket����
rtc.on("connected", function (socket) {
    //����������Ƶ��
    rtc.createStream({
        "video": true,
        "audio": true
    });
});
//����������Ƶ���ɹ�
rtc.on("stream_created", function (stream) {
    document.getElementById('tea_video_template').srcObject = stream;
    document.getElementById('tea_video_template').play();
    // ���ñ��ز������Լ�������
    document.getElementById('tea_video_template').volume = 0.0;
});
//����������Ƶ��ʧ��
rtc.on("stream_create_error", function () {
    alert("create stream failed!");
});
//���յ������û�����Ƶ��
rtc.on('pc_add_stream', function (stream, socketId) {
    var newVideo = document.createElement("video");
    var id = "stu-" + socketId;
    newVideo.setAttribute("class", "video");
    newVideo.setAttribute("autoplay", "autoplay");
    newVideo.setAttribute("id", id);
    videos.appendChild(newVideo);
    rtc.attachStream(stream, id);
});
//ɾ�������û�
rtc.on('remove_peer', function (socketId) {
    var video = document.getElementById('other-' + socketId);
    if (video) {
        video.parentNode.removeChild(video);
    }
});

//����WebSocket������
rtc.connect("wss:139.159.176.78.3000/wss", "123");
