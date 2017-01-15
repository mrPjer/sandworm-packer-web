package hr.fer.zemris.sandworm.packer.web

import hr.fer.zemris.sandworm.packer.Packer
import hr.fer.zemris.sandworm.packer.RemoteLogger
import net.lingala.zip4j.core.ZipFile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files

@RestController
class MainController {

    @PostMapping("/")
    fun pack(
            @RequestParam file: MultipartFile,
            @RequestParam loggerEndpoint: String,
            @RequestParam taskId: String,
            @RequestParam baseImage: String
    ) {
        val outputDirectory = Files.createTempDirectory("packer").toFile()
        val temporaryFile = Files.createTempFile("packer", null).toFile()

        try {
            file.transferTo(temporaryFile)
            ZipFile(temporaryFile).extractAll(outputDirectory.absolutePath)

            Packer(RemoteLogger(taskId, loggerEndpoint)).pack(
                    outputDirectory,
                    baseImage,
                    taskId
            )
        } finally {
            outputDirectory.deleteRecursively()
            temporaryFile.delete()
        }
    }

}
