const fs = require('fs');

var path = './common/src/main/resources/assets/hexgloop/models/item'

// fs.readdir(path, (err, files) => {
//     files.forEach(file => {
//       console.log(file);
//     });
//   });

var overrideObj = [];


// model names: "hexgloop:item/multi_focus_[selected]_[issealed]"
// issealed: 0 = empty, 1 = unsealed, 2 = sealed

for(var s = 0; s < 6; s++){
    for(var e = 0; e < 3; e++){
        var name = "multi_focus_" + s + "_" + e;
        var model = {};
        model.parent = "item/generated";
        model.textures = {};
        model.textures.layer0 = "hexgloop:item/multifocus/multi_focus_base";
        if(e == 1){
            model.textures.layer1 = "hexgloop:item/multifocus/inner_sealed"
            model.textures.layer2 = "hexgloop:item/multifocus/inner_unsealed"
        } else if(e == 2){
            model.textures.layer1 = "hexgloop:item/multifocus/inner_sealed"
            model.textures.layer2 = "hexgloop:item/multifocus/empty"

        } else {
            model.textures.layer1 = "hexgloop:item/multifocus/empty"
            model.textures.layer2 = "hexgloop:item/multifocus/empty"
        }
        for(var i = 0; i < 6; i++){
            if(i == s){
                model.textures["layer" + (i + 3)] = `hexgloop:item/multifocus/outers${i+1}`;
            } else {
                model.textures["layer" + (i + 3)] = `hexgloop:item/multifocus/outer${i+1}`;
            }
        }
        var thisOverride = {};
        thisOverride.predicate = {};
        thisOverride.predicate.selected = (1/6)*s;
        thisOverride.predicate.sealed = e/2;
        thisOverride.model = "hexgloop:item/multi_focus/" + name;
        overrideObj.push(thisOverride);
        fs.writeFile(path + "/multi_focus/" + name + ".json", JSON.stringify(model), function(err) {
        });
    }
}

console.log(overrideObj);