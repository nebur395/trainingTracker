package org.trainingTracker.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.trainingTracker.database.dataAccesObject.ExercisesDAO;
import org.trainingTracker.database.valueObject.ExerciseVO;

/**
 * Servlet implementation class AddExercise
 */
@WebServlet("/addExercise")
public class AddExercise extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddExercise() {
        super();
    }

    @Override
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean error = false;
        String user = "";
        String predefined = "";
        String muscleGroup = "";
        String exercise = "";
        
        // Reads a JSON Object from request and captures his fields
        JSONObject json = null;
        try {
            json = ServletCommon.readJSON(request.getReader());
            user = json.getString("user");
            predefined = json.getString("id");
            muscleGroup = json.getString("muscleGroup");
            exercise = json.getString("name");
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
            error = true;
        }
        
        // Field revision
        if ((muscleGroup==null) || (muscleGroup.trim().equals(""))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Debe seleccionar un grupo muscular");
            error = true;
        }
        if ((exercise==null) || (exercise.trim().equals(""))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Debe seleccionar un ejercicio");
            error = true;
        }
        
        if (!error) {
            try {
                List<ExerciseVO> exercisesList;
                Iterator<ExerciseVO> it;
                ExerciseVO vo;
                
                // Search for user exercises in BD
                exercisesList = ExercisesDAO.listUserExercises(user);
                if (exercisesList == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
                }
                else {
                    it = exercisesList.iterator();
                    while (!error && it.hasNext()) {
                        vo = it.next();
                        // if the new exercise doesn't exists in user's routine
                        if (muscleGroup.equals(vo.getMuscleGroup()) && exercise.equals(vo.getName())) {
                            error = true;
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().write("Este ejercicio ya forma parte de su rutina");
                        }
                    }
                }
                
                if (!error) {
                    // if exercise predefined
                    if (!predefined.equals("0")) {
                        // Add predefined exercise in user's routine and BD
                        if (ExercisesDAO.addDefaultExercise(Integer.parseInt(predefined), user) == -1) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
                        }
                        else {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Ejercicio predefinido añadido");
                        }
                    }
                    // if exercise custom
                    else {
                        exercisesList = ExercisesDAO.listDefaultExercises();
                        if (exercisesList == null) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
                        }
                        else {
                            it = exercisesList.iterator();
                            while (!error && it.hasNext()) {
                                vo = it.next();
                                // if the new exercise doesn't exists as predefined exercise
                                if (muscleGroup.equals(vo.getMuscleGroup()) && exercise.equals(vo.getName())) {
                                    error = true;
                                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                    response.getWriter().write("Este ejercicio forma parte de los predefinidos");
                                }
                            }
                        }
                        
                        if (!error) {
                            // Add custom exercise in BD
                            if (ExercisesDAO.addCustomExercise(exercise, muscleGroup, user) == -1) {
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
                            }
                            else {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.getWriter().write("Ejercicio personalizado añadido");
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Error interno en el servidor. Vuelva intentarlo más tarde");
            }
        }
    }
    
}
