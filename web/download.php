<?php
$pass = $_GET['pass'];

$stored_pass  = file_get_contents("../downloadPassword.txt");

$pwoptions   = ['cost' => 8,]; // all up to you
$storedpasshash = password_hash($stored_pass, PASSWORD_BCRYPT, $pwoptions);  

if(password_verify($pass,$storedpasshash))
{
        include("../data.csv");
}
else
{
    if(isset($_GET))
    {?>

            <form method="GET" action="download.php">
            Pass <input type="password" name="pass"></input><br/>
            <input type="submit" name="submit" value="Go"></input>
            </form>
    <?}
    else{
        echo "Wrong password";
    }
}
?>