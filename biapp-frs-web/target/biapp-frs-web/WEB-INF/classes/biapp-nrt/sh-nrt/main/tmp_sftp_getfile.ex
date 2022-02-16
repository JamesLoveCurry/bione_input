#!/usr/bin/expect -f

#set port 22
#set user urrp
set user [lindex $argv 0]
set password [lindex $argv 1]
set host [lindex $argv 2]
set getFilePath [lindex $argv 3]
set inckey [lindex $argv 4]
set getYtjGrzhBack [lindex $argv 5]


#set host 10.1.244.73
#set host 10.200.250.25
#set password urrp@123

set timeout -1

spawn sftp $user@$host
expect "*assword:*"
send "$password\r"


expect "sftp>"
send "cd ${getYtjGrzhBack}\r"

expect "sftp>"
send "lcd ${getFilePath}\r"

expect "sftp>"
send "mget *$inckey*\r"

expect "sftp>"
send "bye\r"

expect eof

