package com.tugas_day21.controller;

import com.tugas_day21.model.ImageDAO;
import com.tugas_day21.repository.ImageRepo;
import com.tugas_day21.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class IMDbRestController {
    public static final Logger logger = (Logger) LoggerFactory.getLogger(IMDbRestController.class);

    @Autowired
    private ImageRepo imageRepo;

    // POST
    @RequestMapping(value = "/imdb/", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<?> addImage(@RequestBody ImageDAO imageDAO) throws SQLException, ClassNotFoundException {
        logger.info("Post watchlisted image path {}", imageDAO);
        imageRepo.save(imageDAO);
        return new ResponseEntity<>(imageDAO, HttpStatus.CREATED);
    }

    // GET
    @RequestMapping(value = "/imdb/", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<ImageDAO>> getAllImage() throws SQLException, ClassNotFoundException {
        logger.info("Get all watchlist image");
        List<ImageDAO> images = imageRepo.findAll();
        if (images.isEmpty()) {
            return new ResponseEntity<>(images, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    // DELETE A BOOK
    @RequestMapping(value = "/imdb/{id_imdb}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable("id_imdb") String id_imdb) throws SQLException, ClassNotFoundException {
        logger.info("Fetching & Deleting Image with id_imdb : {}", id_imdb);
        ImageDAO imageDAO = imageRepo.findById(id_imdb).orElse(null);
        if (imageDAO == null) {
            logger.error("Unable to delete. Image with id {} not found.", id_imdb);
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. Image with id " + id_imdb
                    + " not found."), HttpStatus.NOT_FOUND);
        }
        imageRepo.deleteById(id_imdb);
        return new ResponseEntity<ImageDAO>(HttpStatus.NO_CONTENT);
    }
}
