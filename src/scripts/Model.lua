require 'src/scripts/Evaluate'
require 'src/scripts/KPCA'
require 'src/scripts/Train'
require 'src/scripts/Validate'

local kpcaEigenVals, kpcaEigenVecs, trainData

local inputFileName = "resources/in.txt"
local outputFileName = "resources/out.txt"

local function sleep(s)
	local ntime = os.time() + s
	repeat until os.time() > ntime
end--sleep()

local function refreshFile(fileName)
	io.open(fileName, "w"):close()
end--refreshFile()

local function setup()
	refreshFile(inputFileName)				--Reset i/o files
	refreshFile(outputFileName)
	
	--TODO load models and whatnot
end--setup()

local function shutdown()
	--TODO
end--shutdown()

setup()
while true do
	local inFile = io.open(inputFileName, "r")
	local line = inFile:read("*line")
	
	if line ~= nil then									--Loop until data is available
		local inputs = {}
		local result
		if line == "kpca" then
			repeat
				line = inFile:read("*line")
				if line ~= nill then
					table.insert(inputs, {})
					for word in string.gmatch(line, '([^,]+)') do
						table.insert(inputs[#inputs], tonumber(word))
					end
				end
			until line == nil
			
			trainData = torch.Tensor(inputs)
			kpcaEigenVals, kpcaEigenVecs = KPCA.computeEigens(trainData)
			
			print(KPCA.project(trainData[1], trainData, kpcaEigenVals, kpcaEigenVecs))
			
			result = "done"
		elseif line == "train" then --TODO can name methods to separate different algorithms here
			--TODO while there are still lines, read input line then read target line
		elseif line == "validate" then
			--TODO while there are still lines, read input line then read target line
		elseif line == "predict" then
			--TODO read next line for values
		elseif line == "done" then
			break
		else
			result = "Unknown option"
		end
	
		outFile = io.open(outputFileName, "w")			--Write result
		outFile:write(result .. "\n")
		outFile:close()
		inFile:close()									--Refresh input file
		refreshFile(inputFileName)
		inFile = io.open(inputFileName, "r")
	end
	
	sleep(0.01)
end

shutdown()