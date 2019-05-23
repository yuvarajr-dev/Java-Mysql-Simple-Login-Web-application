provider "aws" {
region = "ap-south-1"
#access_key = "$(var.AWS_ACCESS_KEY_ID)"
#secret_key = "$(var.AWS_SECRET_ACCESS_KEY)"
}

resource "aws_instance" "linuxnix" {
count = 1
ami = "ami-007d5db58754fa284"
instance_type = "t2.micro"
key_name = "terra"
tags {
Name = "linuxnix-${count.index+1}"
}
}

output "ip" {
value = "${aws_instance.linuxnix.*.public_ip}"
}
