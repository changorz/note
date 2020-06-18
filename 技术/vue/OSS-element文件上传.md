## element组件

```vue
<el-upload
  class="upload-demo"
  :show-file-list="false"           // 不显示文件上传列表
  drag
  action
  accept="image/gif, image/jpeg"    // 文件格式
  :http-request="handleUpload"      // 文件上传函数
>
</el-upload>
```

```js
文件上传
handleUpload (option) {
  // 生成的文件名称
  const objName = this.uuid()
  // 调用 ali-oss 中的方法
  alioos.put(`lbt/${objName}.jpg`, option.file).then(res => {
    this.add(res.url)
  })
},
```

## OSS 文件

**ali-oss.js**

```js
// 文档地址：https://help.aliyun.com/document_detail/64047.html?spm=a2c4g.11186623.6.1340.5e5159660LQlZf
// 引入ali-oss
const OSS = require('ali-oss')

/**
 *  [accessKeyId] {String}：通过阿里云控制台创建的AccessKey。
 *  [accessKeySecret] {String}：通过阿里云控制台创建的AccessSecret。
 *  [bucket] {String}：通过控制台或PutBucket创建的bucket。
 *  [region] {String}：bucket所在的区域， 默认oss-cn-hangzhou。
 */

const client = new OSS({
  region: 'oss-cn-beijing',
  // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，部署在服务端使用RAM子账号或STS，部署在客户端使用STS。
  accessKeyId: 'LT*******************1w',
  accessKeySecret: 'e********************3j3wP',
  bucket: 'c****024'
})

export const put = async (ObjName, flieUrl) => {
  try {
    // object-key可以自定义为文件名（例如file.txt）或目录（例如abc/test/file.txt）的形式，实现将文件上传至当前Bucket或Bucket下的指定目录。
    const result = await client.put(ObjName, flieUrl)
    return result
  } catch (e) {
    console.log('错误：')
    console.log(e)
  }
}
```

