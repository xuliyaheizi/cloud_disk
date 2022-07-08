import request from "@/utils/request";

export function login(formData) {
  return request({
    url: "/doLogin",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data: formData,
  });
}
