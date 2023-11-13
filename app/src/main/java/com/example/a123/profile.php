<?php
if (!empty($_POST['email']) && !empty($POST['apiKey'])){
    $email = $POST['email'];
    $apiKey = $POST['apiKey'];
    $result = array();
    $con = mysqli_connect("localhost","root","","login_register");
    if($con){
        $sql = "select * from users1 where email ='".$email."'and apiKey = '".$apiKey."'";
        $res = mysqli_query($con, $sql);
        if (mysqli_num_rows($res) != 0){
            $row = mysqli_fetch_assoc($res);
            $result = array("status"=>"success","message"=>"data fetched successfully",
                "name"=>$row['name'],"email"=>$row['email'], "apiKey"=> $row['apiKey']);
        }else $result = array("status"=>"failed","message"=>"Unauthorised access");
    }else $result = array("status"=>"failed","message"=>"Database connection has failed");
}else $result = array("status"=>"failed","message"=>"All fields are required");

echo json_encode($result, JSON_PRETTY_PRINT);